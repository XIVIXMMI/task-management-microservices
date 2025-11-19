package com.taskmanagement.userservice.domain.exception;

public class InvalidRefreshTokenException extends RuntimeException{
    public InvalidRefreshTokenException(String message){
        super(message);
    }
}
