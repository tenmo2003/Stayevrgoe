package com.group12.stayevrgoe.messaging;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("chats")
public class Chat {
    @Id
    private String id;
    private String customerId;
    private String hotelId;
    private String lastMessageId;

    @LastModifiedDate
    private Date lastModifiedDate;
}
