package com.group12.stayevrgoe.user;

import com.group12.stayevrgoe.hotel.HotelRoom;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author anhvn
 */
@Data
@Document(collection = "booking_histories")
public class BookingHistory {
    @Id
    private String id;

    private String userEmail;
    private Date from;
    private Date to;
    @DBRef(lazy = true)
    private HotelRoom hotelRoom;
}
