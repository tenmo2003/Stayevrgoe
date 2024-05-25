package com.group12.stayevrgoe.hotel.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.EnumSet;
import java.util.List;

/**
 * @author anhvn
 */
@Data
public class HotelRegisterDTO {
    private String name;
    private String location;
    private String description;
    private List<MultipartFile> images;
    private EnumSet<HotelFacility> facilities;
}
