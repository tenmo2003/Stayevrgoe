package com.group12.stayevrgoe.hotel;

import lombok.Builder;
import lombok.Data;
import org.joda.time.Interval;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.EnumSet;
import java.util.List;

@Document("hotel_rooms")
@Data
@Builder
public class HotelRoom {
    @Id
    private String id;
    private String hotelId;
    private EnumSet<HotelRoomFacility> facilities;
    private float priceInUSD;
    private List<Interval> takenIntervals;
}
