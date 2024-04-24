package com.group12.stayevrgoe.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class User {
    @Id
    private String id;

    private String email;
    @JsonIgnore
    private String password;

    private UserRole role;
}
