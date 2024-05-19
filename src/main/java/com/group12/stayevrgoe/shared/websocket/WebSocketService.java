package com.group12.stayevrgoe.shared.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate template;

    public void send(String destinationTopic, String message) {
        template.convertAndSend(destinationTopic, message);
    }
}
