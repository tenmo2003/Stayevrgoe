package com.group12.stayevrgoe.hotel.entity;

import lombok.Builder;
import lombok.Data;
import org.joda.time.Interval;

@Data
@Builder
public class HotelRoomBooking {
    private Interval interval;
    private String bookHistoryId;
}
