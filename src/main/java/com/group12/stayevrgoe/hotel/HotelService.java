package com.group12.stayevrgoe.hotel;

import com.group12.stayevrgoe.shared.exceptions.BusinessException;
import com.group12.stayevrgoe.shared.utils.AuthenticationUtils;
import com.group12.stayevrgoe.user.BookingHistory;
import com.group12.stayevrgoe.user.BookingHistoryDAO;
import lombok.RequiredArgsConstructor;
import org.joda.time.Interval;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final HotelDAO hotelDAO;
    private final HotelRoomDAO hotelRoomDAO;
    private final BookingHistoryDAO bookingHistoryDAO;

    public List<Hotel> getHotels(HotelFilter filter, Pageable pageable) {
        return hotelDAO.get(filter, pageable);
    }

    public BookingHistory bookHotelRoom(HotelRoomBookDTO dto) {
        HotelRoom hotelRoom = hotelRoomDAO.getByUniqueAttribute(dto.getRoomId());
        if (!isRoomAvailableInDateRange(hotelRoom, dto.getFrom(), dto.getTo())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Room is not available in date range");
        }

        BookingHistory bookingHistory = new BookingHistory();
        bookingHistory.setUserEmail(AuthenticationUtils.getCurrentUser().getEmail());
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
        BookingHistory bookingHistory = bookingHistoryDAO.getByUniqueAttribute(bookingId);

        HotelRoom hotelRoom = hotelRoomDAO.getByUniqueAttribute(bookingHistory.getHotelRoomId());
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
}
