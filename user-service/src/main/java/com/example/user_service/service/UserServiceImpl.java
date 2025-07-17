package com.example.user_service.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.LoginUserResponse;
import com.example.user_service.dto.RegisterRequest;
import com.example.user_service.dto.UserProfileResponse;
import com.example.user_service.dto.RegisterUserResponse;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public RegisterUserResponse register(RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()) != null
                || userRepository.findByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException("Username or email already exists.");
        } else {

            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setFirstName(request.getFirstName());
            newUser.setLastName(request.getLastName());

            User savedUser = userRepository.save(newUser);

            return new RegisterUserResponse(savedUser.getId(), savedUser.getUsername(), "User registered successfully.");

        }

    }

    @Override
    public LoginUserResponse login(LoginRequest request) throws IllegalAccessException {

        User user = userRepository.findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalAccessException("Invalid username or password");
        } else {
            return new LoginUserResponse(user.getId(), user.getUsername());
        }

    }

    @Override
    public UserProfileResponse getProfileById(UUID userId) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new EntityNotFoundException("User with ID " + userId + " not found.");
        } else {
            return new UserProfileResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName());
        }

    }

}
