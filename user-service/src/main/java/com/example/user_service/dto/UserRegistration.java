package com.example.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegistration {

    @NotBlank(message = "username should not be blank")
    private String username;

    @NotBlank(message = "password should not be blank")
    private String password;

    @NotBlank(message = "email should not be blank")
    @Email
    private String email;

    @NotBlank(message = "first name should not be blank")
    private String firstName;

    @NotBlank(message = "last name should not be blank")
    private String lastName;

}
