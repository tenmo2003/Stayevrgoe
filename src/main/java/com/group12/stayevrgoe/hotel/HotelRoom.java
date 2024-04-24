package com.group12.stayevrgoe.hotel;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("hotel_rooms")
@Data
public class HotelRoom {
    @Id
    private String id;
    private String hotelId;
}
