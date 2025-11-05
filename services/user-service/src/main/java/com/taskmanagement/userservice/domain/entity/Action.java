package com.taskmanagement.userservice.domain.entity;

public enum Action {
    READ, // View,list resource
    WRITE, // Create, update resource
    DELETE, // Delete resource
    MANAGE // Full control (assigned user, config task ... )
}
