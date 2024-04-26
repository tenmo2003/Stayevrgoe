package com.group12.stayevrgoe.shared.network;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ApiResponse {
    private int code;
    private String message;
    private Object data;

    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResponse(HttpStatus status, String message) {
        this(status.value(), message);
    }

    public ApiResponse(HttpStatus status, String message, Object data) {
        this(status.value(), message, data);
    }
}
