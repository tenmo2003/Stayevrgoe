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
}
