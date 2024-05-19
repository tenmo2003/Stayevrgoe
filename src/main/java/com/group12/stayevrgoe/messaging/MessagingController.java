package com.group12.stayevrgoe.messaging;

import com.group12.stayevrgoe.shared.network.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessagingController {
    private final MessagingService messagingService;
    @PostMapping
    public ApiResponse sendMessage(NewMessageDTO dto) {
        return new ApiResponse(HttpStatus.OK, "Message sent", messagingService.sendMessage(dto));
    }
}
