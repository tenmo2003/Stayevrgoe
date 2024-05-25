package com.group12.stayevrgoe.hotel.entity;

import lombok.Data;

import java.util.EnumSet;

/**
 * @author anhvn
 */
@Data
public class HotelEditDTO {
    private String name;
    private String location;
    private String description;
    private EnumSet<HotelFacility> facilities;
    // NOTE: skipping images edit
}
