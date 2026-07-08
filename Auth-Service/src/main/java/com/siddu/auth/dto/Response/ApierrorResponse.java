package com.siddu.auth.dto.Response;

import lombok.Getter;

@Getter
public class ApierrorResponse {
    private  final String message;
    private final String error;
    public  ApierrorResponse(String error,String message){
        this.message=message;
        this.error=error;
    }
}
