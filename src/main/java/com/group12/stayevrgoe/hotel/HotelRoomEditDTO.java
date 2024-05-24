package com.group12.stayevrgoe.hotel;

import lombok.Data;

import java.util.EnumSet;

/**
 * @author anhvn
 */
@Data
public class HotelRoomEditDTO {
    private String id;
    private String description;
    private EnumSet<HotelRoomFacility> facilities;
    private float priceInUSD;
}
