package com.group12.stayevrgoe.notification;

import lombok.Data;

/**
 * @author anhvn
 */
@Data
public class NewNotificationDTO {
    private NotificationType type;
    private String sentTo;
    private String header;
    private String content;
}
