package com.group12.stayevrgoe.rating.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("ratings")
@Builder
public class Rating {
    @Id
    private String id;

    private String hotelRoomId;
    private String userId;

    private int value;
    private String comment;

    private RatingResponse hotelResponse;

    @CreatedDate
    private Date createdDate;
}
