package com.group12.stayevrgoe.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.EnumSet;
import java.util.List;

@Document("hotels")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
