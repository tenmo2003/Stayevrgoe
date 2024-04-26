package com.group12.stayevrgoe.hotel;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.EnumSet;

@Data
@AllArgsConstructor
public class HotelFilter {
    private String name;
    private String location;
    private float minPrice;
    private float maxPrice;
    private EnumSet<HotelFacility> facilities;
}
