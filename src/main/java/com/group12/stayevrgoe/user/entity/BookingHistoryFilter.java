package com.group12.stayevrgoe.user.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author anhvn
 */
@Getter
@Setter
public class BookingHistoryFilter {
    private String userId;
    private String hotelRoomId;
    private Date from;
    private Date to;
}
