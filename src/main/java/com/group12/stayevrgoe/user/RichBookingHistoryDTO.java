package com.group12.stayevrgoe.user;

import com.group12.stayevrgoe.hotel.Hotel;
import com.group12.stayevrgoe.hotel.HotelRoom;
import lombok.Data;

import java.util.Date;

/**
 * @author anhvn
 */
@Data
public class RichBookingHistoryDTO {
    private String id;

    private User user;
    private Date from;
    private Date to;
    private Hotel hotel;
    private HotelRoom hotelRoom;

    public BookingHistory toBookingHistory() {
        BookingHistory bookingHistory = new BookingHistory();
        bookingHistory.setId(id);
        bookingHistory.setUserId(user.getId());
        bookingHistory.setFrom(from);
        bookingHistory.setTo(to);
        bookingHistory.setHotelId(hotel.getId());
        bookingHistory.setHotelRoomId(hotelRoom.getId());
        return bookingHistory;
    }
}
