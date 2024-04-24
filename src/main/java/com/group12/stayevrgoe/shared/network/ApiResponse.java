package com.group12.stayevrgoe.shared.network;

import lombok.AllArgsConstructor;
import lombok.Data;

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
}
