package com.group12.stayevrgoe.rating;

import com.group12.stayevrgoe.hotel.HotelRoom;
import com.group12.stayevrgoe.user.User;
import lombok.Data;

import java.util.Date;

/**
 * @author anhvn
 */
@Data
public class RichRatingDTO {
    private String id;

    private HotelRoom hotelRoom;
    private User user;

    private int value;
    private String comment;

    private RatingResponse hotelResponse;
    private Date createdDate;

    public Rating convertToRating() {
        return Rating.builder()
                .id(id)
                .hotelRoomId(hotelRoom.getId())
                .userId(user.getId())
                .value(value)
                .comment(comment)
                .hotelResponse(hotelResponse)
                .createdDate(createdDate)
                .build();
    }
}
