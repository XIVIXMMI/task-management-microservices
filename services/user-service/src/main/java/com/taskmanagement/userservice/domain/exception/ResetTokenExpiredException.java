package com.taskmanagement.userservice.domain.exception;

public class ResetTokenExpiredException extends ResetTokenNotFoundException{
    public ResetTokenExpiredException(String message) {
        super(message);
    }
}
