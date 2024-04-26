package com.group12.stayevrgoe.hotel;

import com.group12.stayevrgoe.shared.exceptions.BusinessException;
import com.group12.stayevrgoe.shared.utils.AuthenticationUtils;
import com.group12.stayevrgoe.user.BookingHistory;
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
        hotelRoom.getTakenIntervals().add(new Interval(dto.getFrom().getTime(), dto.getTo().getTime()));
        hotelRoomDAO.save(hotelRoom);

        return bookingHistory;
    }

    private boolean isRoomAvailableInDateRange(HotelRoom room, Date from, Date to) {
        Interval requestedInterval = new Interval(from.getTime(), to.getTime());
        for (Interval takenInterval : room.getTakenIntervals()) {
            if (requestedInterval.overlaps(takenInterval)) {
                return false;
            }
        }
        return true;
    }
}
