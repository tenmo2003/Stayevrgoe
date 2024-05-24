package com.group12.stayevrgoe.authentication;

import com.group12.stayevrgoe.user.UserRole;
import lombok.Data;

@Data
public class SignUpDTO {
    private String email;
    private String password;
    private UserRole role;
}
