package com.group12.stayevrgoe.messaging.boundary;

import com.group12.stayevrgoe.messaging.control.MessagingService;
import com.group12.stayevrgoe.messaging.entity.MessageFilter;
import com.group12.stayevrgoe.messaging.entity.NewMessageDTO;
import com.group12.stayevrgoe.shared.network.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessagingController {
    private final MessagingService messagingService;
    @PostMapping("/message")
    public ApiResponse sendMessage(NewMessageDTO dto) {
        return new ApiResponse(HttpStatus.OK, "Message sent", messagingService.sendMessage(dto));
    }

    @GetMapping("/messages")
    public ApiResponse getMessages(@RequestParam String chatId,
                                   @RequestParam(name="page") int pageNumber,
                                   @RequestParam(name="size") int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        MessageFilter messageFilter = new MessageFilter(chatId);

        return new ApiResponse(HttpStatus.OK, "OK", messagingService.getMessages(messageFilter, pageable));
    }

    @GetMapping("/chats")
    public ApiResponse getChats(@RequestParam(name="page") int pageNumber,
                               @RequestParam(name="size") int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return new ApiResponse(HttpStatus.OK, "OK", messagingService.getChats(pageable));
    }
}
