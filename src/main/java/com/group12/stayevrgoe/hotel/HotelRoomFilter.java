package com.group12.stayevrgoe.hotel;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.EnumSet;

/**
 * @author anhvn
 */
@Data
@AllArgsConstructor
public class HotelRoomFilter {
    private String hotelId;
    private EnumSet<HotelRoomFacility> facilities;
    private float minPrice;
    private float maxPrice;
}
