package com.group12.stayevrgoe.messaging;

import com.group12.stayevrgoe.shared.utils.AuthenticationUtils;
import com.group12.stayevrgoe.shared.websocket.WebSocketService;
import com.nimbusds.jose.shaded.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessagingService {
    private final WebSocketService webSocketService;
    private final MessageDAO dao;
    public Message sendMessage(NewMessageDTO dto) {
        Message message = new Message();
        message.setChatId(dto.getChatId());
        message.setContent(dto.getContent());
        message.setSenderEmail(AuthenticationUtils.getCurrentUser().getEmail());
        message.setType(MessageType.TEXT);

        message = dao.save(message);

        Gson gson = new Gson();
        webSocketService.send("message", gson.toJson(message));

        return message;
    }

    public List<Message> getMessages(MessageFilter filter, Pageable pageable) {
        return dao.get(filter, pageable);
    }
}
