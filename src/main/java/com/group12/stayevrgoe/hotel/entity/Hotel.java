package com.group12.stayevrgoe.hotel.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.EnumSet;
import java.util.List;

@Data
@Builder
@Document("hotels")
public class Hotel {
    @Id
    private String id;
    private String name;
    private String location;
    private String description;
    private List<String> imagesURLs;
    private EnumSet<HotelFacility> facilities;
    private float minPriceInUSD;
    private float maxPriceInUSD;
    private boolean listed = false;
    private String registrantId;
    private int totalRating;
    private int ratingCount;
}
