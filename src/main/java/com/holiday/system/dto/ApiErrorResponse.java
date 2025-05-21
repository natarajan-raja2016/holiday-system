package com.holiday.system.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ApiErrorResponse(LocalDateTime now, int status, String error, String msg, String requestURI) {
        this.timestamp = now;
        this.status = status;
        this.error = error;
        this.message = msg;
        this.path = requestURI;
	}

}