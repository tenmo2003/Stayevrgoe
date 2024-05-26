package com.group12.stayevrgoe.hotel.control;

import com.group12.stayevrgoe.hotel.entity.*;
import com.group12.stayevrgoe.shared.exceptions.BusinessException;
import com.group12.stayevrgoe.shared.utils.AuthenticationUtils;
import com.group12.stayevrgoe.shared.utils.ImgurUtils;
import com.group12.stayevrgoe.user.control.BookingHistoryDAO;
import com.group12.stayevrgoe.user.control.UserDAO;
import com.group12.stayevrgoe.user.entity.BookingHistory;
import com.group12.stayevrgoe.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.joda.time.Interval;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final HotelDAO hotelDAO;
    private final HotelRoomDAO hotelRoomDAO;
    private final BookingHistoryDAO bookingHistoryDAO;
    private final UserDAO userDAO;

    public List<Hotel> getHotels(HotelFilter filter, Pageable pageable) {
        return hotelDAO.get(filter, pageable);
    }

    public BookingHistory bookHotelRoom(HotelRoomBookDTO dto) {
        HotelRoom hotelRoom = hotelRoomDAO.getById(dto.getRoomId());
        if (!isRoomAvailableInDateRange(hotelRoom, dto.getFrom(), dto.getTo())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Room is not available in date range");
        }

        BookingHistory bookingHistory = new BookingHistory();
        bookingHistory.setUserId(AuthenticationUtils.getCurrentUser().getId());
        bookingHistory.setHotelId(hotelRoom.getHotelId());
        bookingHistory.setHotelRoomId(hotelRoom.getHotelId());
        bookingHistory.setFrom(dto.getFrom());
        bookingHistory.setTo(dto.getTo());

        bookingHistory = bookingHistoryDAO.save(bookingHistory);
        hotelRoom.getCurrentBookings().add(
                HotelRoomBooking.builder()
                        .bookHistoryId(bookingHistory.getId())
                        .interval(new Interval(dto.getFrom().getTime(), dto.getTo().getTime()))
                        .build());
        hotelRoomDAO.save(hotelRoom);
        return bookingHistory;
    }

    public void cancelBooking(String bookingId) {
        BookingHistory bookingHistory = bookingHistoryDAO.getById(bookingId);

        HotelRoom hotelRoom = hotelRoomDAO.getById(bookingHistory.getHotelRoomId());
        for (int i = 0; i < hotelRoom.getCurrentBookings().size(); i++) {
            if (hotelRoom.getCurrentBookings().get(i).getBookHistoryId().equals(bookingId)) {
                hotelRoom.getCurrentBookings().remove(i);
                break;
            }
        }
        hotelRoomDAO.save(hotelRoom);

        bookingHistoryDAO.delete(bookingId);
    }

    public List<HotelRoom> getHotelRooms(HotelRoomFilter filter, Pageable pageable) {
        return hotelRoomDAO.get(filter, pageable);
    }

    public void registerNewHotel(HotelRegisterDTO dto) {
        User user = AuthenticationUtils.getCurrentUser();

        Hotel newHotel = Hotel.builder()
                .name(dto.getName())
                .location(dto.getLocation())
                .description(dto.getDescription())
                .imagesURLs(ImgurUtils.uploadMultipleImages(dto.getImages()))
                .facilities(dto.getFacilities())
                .listed(false)
                .registrantId(user.getId())
                .ratingCount(0)
                .totalRating(0)
                .build();

        newHotel = hotelDAO.save(newHotel);
        user.setWorkingHotelId(newHotel.getId());

        userDAO.save(user);
    }

    public void approveHotel(String id) {
        Hotel hotel = hotelDAO.getById(id);
        hotel.setListed(true);
        hotelDAO.save(hotel);
    }

    public void deleteHotel(String id) {
        hotelDAO.delete(id);
    }

    public HotelRoom addNewHotelRoom(HotelRoomAddDTO dto) {
        HotelRoom room = HotelRoom.builder()
                .hotelId(dto.getHotelId())
                .description(dto.getDescription())
                .facilities(dto.getFacilities())
                .capacity(dto.getCapacity())
                .area(dto.getArea())
                .currentBookings(new ArrayList<>())
                .priceInUSD(dto.getPriceInUSD())
                .imagesURLs(ImgurUtils.uploadMultipleImages(dto.getImages()))
                .totalRating(0)
                .ratingCount(0)
                .build();

        updateHotelPriceRange(dto.getHotelId(), dto.getPriceInUSD());

        return hotelRoomDAO.save(room);
    }

    public void editHotelRoomInfo(HotelRoomEditDTO dto) {
        HotelRoom room = hotelRoomDAO.getById(dto.getId());
        room.setDescription(dto.getDescription());
        room.setFacilities(dto.getFacilities());
        room.setPriceInUSD(dto.getPriceInUSD());
        hotelRoomDAO.save(room);

        updateHotelPriceRange(room.getHotelId(), dto.getPriceInUSD());
    }

    public void deleteHotelRoom(String id) {
        hotelRoomDAO.delete(id);
    }

    private void updateHotelPriceRange(String hotelId, float price) {
        Hotel hotel = hotelDAO.getById(hotelId);
        hotel.setMinPriceInUSD(Math.min(hotel.getMinPriceInUSD(), price));
        hotel.setMaxPriceInUSD(Math.max(hotel.getMaxPriceInUSD(), price));
        hotelDAO.save(hotel);
    }
    private boolean isRoomAvailableInDateRange(HotelRoom room, Date from, Date to) {
        Interval requestedInterval = new Interval(from.getTime(), to.getTime());
        for (HotelRoomBooking booking : room.getCurrentBookings()) {
            Interval bookingInterval = booking.getInterval();
            if (requestedInterval.overlaps(bookingInterval)) {
                return false;
            }
        }
        return true;
    }

    public Hotel getHotelById(String id) {
        return hotelDAO.getById(id);
    }

    public HotelRoom getHotelRoomById(String id) {
        return hotelRoomDAO.getById(id);
    }
}
