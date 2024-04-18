package com.group12.stayevrgoe.user;

import com.group12.stayevrgoe.shared.http.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping("/hello")
    public ApiResponse hello() {
        return new ApiResponse(HttpStatus.OK.value(), "Hello");
    }

    @GetMapping("/admin")
    public ApiResponse admin() {
        return new ApiResponse(HttpStatus.OK.value(), "Admin");
    }
}
