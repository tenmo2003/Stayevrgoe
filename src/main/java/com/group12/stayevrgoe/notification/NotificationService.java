package com.group12.stayevrgoe.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author anhvn
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationDAO notificationDAO;

    public List<Notification> getNotifications(NotificationFilter filter, Pageable pageable) {
        return notificationDAO.get(filter, pageable);
    }

    public Notification sendNotification(NewNotificationDTO dto) {
        return notificationDAO.save(Notification.builder()
                .type(dto.getType())
                .sentTo(dto.getSentTo())
                .header(dto.getHeader())
                .content(dto.getContent())
                .build());
    }
}
