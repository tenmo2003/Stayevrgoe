package com.group12.stayevrgoe.shared.configs;


import com.group12.stayevrgoe.shared.exceptions.BusinessException;
import com.group12.stayevrgoe.shared.http.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ApiResponse handleBusinessException(BusinessException e) {
        return new ApiResponse(e.getStatus().value(), e.getMessage());
    }
}
