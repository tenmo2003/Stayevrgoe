package com.group12.stayevrgoe.notification;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author anhvn
 */
@Data
@Document("notifications")
@Builder
public class Notification {
    @Id
    private String id;

    private NotificationType type;
    // link to userId or hotelId
    private String sentTo;
    private String header;
    private String content;
    @CreatedDate
    private Date sentAt;
}
