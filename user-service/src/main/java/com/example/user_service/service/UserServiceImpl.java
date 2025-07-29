package com.example.user_service.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.user_service.dto.UserLogin;
import com.example.user_service.dto.LoginResponse;
import com.example.user_service.dto.UserRegistration;
import com.example.user_service.dto.UserProfile;
import com.example.user_service.dto.UserResponse;
import com.example.user_service.exception.UserAlreadyExistsException;
import com.example.user_service.exception.UserProfileNotFoundException;
import com.example.user_service.exception.InvalidCredentialsException;

import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserResponse register(UserRegistration request) {

        if (userRepository.findByUsername(request.getUsername()) != null
                || userRepository.findByEmail(request.getEmail()) != null) {
            throw new UserAlreadyExistsException("Username or email already exists.");
        } else {

            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setFirstName(request.getFirstName());
            newUser.setLastName(request.getLastName());

            User savedUser = userRepository.save(newUser);

            return new UserResponse(savedUser.getId(), savedUser.getUsername(), "User registered successfully.");

        }

    }

    @Override
    public LoginResponse login(UserLogin request) {

        User user = userRepository.findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invald username or password.");
        } else {
            return new LoginResponse(user.getId(), user.getUsername());
        }

    }

    @Override
    public UserProfile getProfileById(UUID userId) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UserProfileNotFoundException("User with ID " + userId + " not found.");
        } else {
            return new UserProfile(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName());
        }

    }

}
