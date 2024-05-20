package com.group12.stayevrgoe.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@Data
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;
    @JsonIgnore
    private String password;

    private UserRole role;
}
