package com.example.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLogin {

    @NotBlank(message = "username should not be blank")
    private String username;

    @NotBlank(message = "password should not be blank")
    private String password;

}
