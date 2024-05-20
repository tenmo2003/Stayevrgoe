package com.group12.stayevrgoe.messaging;

import com.group12.stayevrgoe.shared.utils.AuthenticationUtils;
import com.group12.stayevrgoe.shared.websocket.WebSocketService;
import com.group12.stayevrgoe.user.User;
import com.group12.stayevrgoe.user.UserRole;
import com.nimbusds.jose.shaded.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessagingService {
    private final WebSocketService webSocketService;
    private final MessageDAO messageDAO;
    private final ChatDAO chatDAO;
    public Message sendMessage(NewMessageDTO dto) {
        Message message = new Message();
        message.setChatId(dto.getChatId());
        message.setContent(dto.getContent());
        message.setSenderId(AuthenticationUtils.getCurrentUser().getId());
        message.setType(MessageType.TEXT);

        message = messageDAO.save(message);

        RichChatDTO chat = chatDAO.getByUniqueAttribute(dto.getChatId());
        chat.setLastMessage(message);

        chatDAO.save(chat);

        Gson gson = new Gson();
        webSocketService.send("message", gson.toJson(message));

        return message;
    }

    public List<Message> getMessages(MessageFilter filter, Pageable pageable) {
        return messageDAO.get(filter, pageable);
    }

    public List<RichChatDTO> getChats(Pageable pageable) {
        User currentUser = AuthenticationUtils.getCurrentUser();
        ChatFilter chatFilter = new ChatFilter();
        if (currentUser.getRole().equals(UserRole.USER)) {
            chatFilter.setCustomerId(currentUser.getId());
        } else if (currentUser.getRole().equals(UserRole.HOTEL_MANAGER)) {
            chatFilter.setHotelId(currentUser.getId());
        }

        return chatDAO.get(chatFilter, pageable);
    }
}
