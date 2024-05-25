package com.group12.stayevrgoe.notification.boundary;

import com.group12.stayevrgoe.notification.control.NotificationService;
import com.group12.stayevrgoe.notification.entity.NewNotificationDTO;
import com.group12.stayevrgoe.notification.entity.NotificationFilter;
import com.group12.stayevrgoe.notification.entity.NotificationType;
import com.group12.stayevrgoe.shared.network.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author anhvn
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ApiResponse getNotifications(@RequestParam(required = false) String sentTo,
                                        @RequestParam(required = false) NotificationType type,
                                        @RequestParam(name = "page") int pageNumber,
                                        @RequestParam(name = "size") int pageSize) {
        NotificationFilter filter = new NotificationFilter(sentTo, type);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return new ApiResponse(HttpStatus.OK, "OK", notificationService.getNotifications(filter, pageable));
    }

    @PostMapping("/notification")
    public ApiResponse sendNotification(@RequestBody NewNotificationDTO dto) {
        return new ApiResponse(HttpStatus.OK, "OK", notificationService.sendNotification(dto));
    }
}
