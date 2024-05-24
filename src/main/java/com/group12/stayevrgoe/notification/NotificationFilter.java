package com.group12.stayevrgoe.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author anhvn
 */
@Data
@AllArgsConstructor
public class NotificationFilter {
    private String sentTo;
    private NotificationType type;
}
