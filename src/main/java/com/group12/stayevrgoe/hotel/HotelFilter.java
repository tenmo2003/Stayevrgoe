package com.group12.stayevrgoe.hotel;

import lombok.Data;

import java.util.EnumSet;

@Data
public class HotelFilter {
    private String name;
    private String location;
    private float minPrice;
    private float maxPrice;
    private EnumSet<HotelFacility> facilities;
}
