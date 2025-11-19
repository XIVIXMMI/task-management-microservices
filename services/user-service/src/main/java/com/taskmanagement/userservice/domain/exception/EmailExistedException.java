package com.taskmanagement.userservice.domain.exception;

public class EmailExistedException extends RuntimeException {
    public EmailExistedException(String message){
        super(message);
    }
}
