package com.example.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "username should not be blank")
    private String username;

    @NotBlank(message = "password should not be blank")
    private String password;

}
