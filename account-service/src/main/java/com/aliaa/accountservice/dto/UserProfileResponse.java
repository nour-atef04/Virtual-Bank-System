package com.aliaa.accountservice.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class UserProfileResponse {
    private UUID userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}