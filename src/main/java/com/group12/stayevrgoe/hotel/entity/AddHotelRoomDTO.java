package com.group12.stayevrgoe.hotel.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.EnumSet;
import java.util.List;

/**
 * @author anhvn
 */
@Data
public class AddHotelRoomDTO {
    private String hotelId;
    private String description;
    private List<MultipartFile> images;
    private EnumSet<HotelRoomFacility> facilities;
    private float priceInUSD;
    private int capacity;
    private float area;
}
