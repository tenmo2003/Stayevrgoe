package com.group12.stayevrgoe.messaging.entity;

import lombok.Data;

@Data
public class NewMessageDTO {
    private String content;
    private MessageType messageType;
    private String chatId;
}
