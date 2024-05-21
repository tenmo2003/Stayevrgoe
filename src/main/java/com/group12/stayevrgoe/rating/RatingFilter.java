package com.group12.stayevrgoe.rating;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author anhvn
 */
@Data
@AllArgsConstructor
public class RatingFilter {
    private String hotelRoomId;
    private String userId;
    private int value;
    private Date from;
    private Date to;
}
