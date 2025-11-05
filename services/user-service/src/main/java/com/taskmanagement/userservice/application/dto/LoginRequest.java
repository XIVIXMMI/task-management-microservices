package com.taskmanagement.userservice.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @Email @NotBlank String email,

        @NotBlank String password
) {
    // prevent exposed password in plaintext
    @Override
    public String toString(){
        return "email: " + email + "\n " + "password: ********";
    }
}
