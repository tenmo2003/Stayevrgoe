package com.group12.stayevrgoe.hotel;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.EnumSet;
import java.util.List;

@Data
@Builder
@Document("hotel_rooms")
public class HotelRoom {
    @Id
    private String id;
    private String hotelId;
    private String description;
    private List<String> imagesURLs;
    private EnumSet<HotelRoomFacility> facilities;
    private float priceInUSD;
    private List<HotelRoomBooking> currentBookings;
}
