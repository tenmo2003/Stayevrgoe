package com.group12.stayevrgoe.messaging.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("messages")
public class Message {
    @Id
    private String id;

    private String senderId;
    private String chatId;
    private MessageType type = MessageType.TEXT;
    private String content;

    @CreatedDate
    private Date sentAt;
}
