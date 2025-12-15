package com.taskmanagement.userservice.domain.exception;

public class ResetTokenExpiredException extends RuntimeException{
    public ResetTokenExpiredException(String message) {
        super(message);
    }
}
