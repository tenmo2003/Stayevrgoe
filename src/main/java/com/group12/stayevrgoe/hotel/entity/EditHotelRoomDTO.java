package com.group12.stayevrgoe.hotel.entity;

import lombok.Data;

import java.util.EnumSet;

/**
 * @author anhvn
 */
@Data
public class EditHotelRoomDTO {
    private String id;
    private String description;
    private EnumSet<HotelRoomFacility> facilities;
    private float priceInUSD;
}
