package com.group12.stayevrgoe.authentication.entity;

import com.group12.stayevrgoe.user.entity.UserRole;
import lombok.Data;

@Data
public class SignUpDTO {
    private String email;
    private String password;
    private UserRole role;
}
