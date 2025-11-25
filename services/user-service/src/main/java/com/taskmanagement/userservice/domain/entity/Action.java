package com.taskmanagement.userservice.domain.entity;

public enum Action {
    CREATE,  // Create new resource
    READ,    // View/list resource
    UPDATE,  // Modify resource
    DELETE,  // Delete resource
    MANAGE   // Full control (includes all above + assign, configure, etc.)
}