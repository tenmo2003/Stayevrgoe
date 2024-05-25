package com.group12.stayevrgoe.hotel.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author anhvn
 */
@Data
public class HotelRoomBookDTO {
    private String roomId;
    private Date from;
    private Date to;
}
