package com.taskmanagement.userservice.domain.exception;

public class RoleNotFoundException extends RuntimeException{
    public RoleNotFoundException (String message) {
        super(message);
    }
}
