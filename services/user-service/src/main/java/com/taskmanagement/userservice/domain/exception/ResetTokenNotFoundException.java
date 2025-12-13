package com.taskmanagement.userservice.domain.exception;

public class ResetTokenNotFoundException extends RuntimeException{
    public ResetTokenNotFoundException(String message) {
        super(message);
    }
}
