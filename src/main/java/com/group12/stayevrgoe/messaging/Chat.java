package com.group12.stayevrgoe.messaging;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("chats")
@Data
public class Chat {
    @Id
    private String id;
    private String customerId;
    private String hotelId;
    private String lastMessageId;
}
