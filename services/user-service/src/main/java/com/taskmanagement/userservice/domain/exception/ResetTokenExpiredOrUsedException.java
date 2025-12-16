package com.taskmanagement.userservice.domain.exception;

public class ResetTokenExpiredOrUsedException extends RuntimeException{
    public ResetTokenExpiredOrUsedException(String message) {
        super(message);
    }
}
