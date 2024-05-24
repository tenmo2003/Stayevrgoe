package com.group12.stayevrgoe.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("users")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;
    @JsonIgnore
    private String password;

    private String fullName;
    private String phoneNumber;

    private UserRole role;
    private String workingHotelId;
}
