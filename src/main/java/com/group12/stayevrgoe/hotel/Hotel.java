package com.group12.stayevrgoe.hotel;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.EnumSet;

@Document("hotels")
@Data
public class Hotel {
    @Id
    private String id;
    private String name;
    private String location;
    private String description;
    private EnumSet<HotelFacility> facilities;
    private float startPriceInUSD;
}
