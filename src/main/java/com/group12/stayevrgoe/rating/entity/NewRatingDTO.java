package com.group12.stayevrgoe.rating.entity;

import lombok.Data;

/**
 * @author anhvn
 */
@Data
public class NewRatingDTO {
    private String hotelRoomId;
    private int value;
    private String comment;
}
