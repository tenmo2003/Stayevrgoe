package com.group12.stayevrgoe.user;

import lombok.Data;

@Data
public class UserFilter {
    private String name;
    private String email;
    private UserRole role;
}
