package com.group12.stayevrgoe.messaging;

import com.group12.stayevrgoe.hotel.Hotel;
import com.group12.stayevrgoe.user.User;
import lombok.Data;

/**
 * @author anhvn
 */
@Data
public class RichChatDTO {
    private String id;
    private User customer;
    private Hotel hotel;
    private Message lastMessage;

    public Chat convertToChat() {
        Chat chat = new Chat();
        chat.setId(id);
        chat.setCustomerId(customer.getId());
        chat.setHotelId(hotel.getId());
        chat.setLastMessageId(lastMessage.getId());
        return chat;
    }
}
