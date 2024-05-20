package com.group12.stayevrgoe.rating;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("ratings")
@Data
public class Rating {
    @Id
    private String id;

    private String hotelRoomId;
    private String userId;

    private int value;
    private String comment;

    @CreatedDate
    private Date createdDate;
}
