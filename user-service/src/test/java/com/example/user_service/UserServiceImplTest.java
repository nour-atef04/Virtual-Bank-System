package com.example.user_service;

import com.example.user_service.dto.*;
import com.example.user_service.exception.*;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    // @BeforeEach //makes sure this setup runs before each test
    // void setUp() {
    // userRepository = mock(UserRepository.class); //fake user repo
    // passwordEncoder = new BCryptPasswordEncoder();
    // userService = new UserServiceImpl();
    // userService.userRepository = userRepository;
    // userService.passwordEncoder = passwordEncoder;
    // }

    @Test
    void register_shouldReturnUserResponse_whenNewUser() {
        UserRegistration registration = new UserRegistration("john", "1234", "john@example.com", "John", "Doe");

        // simulates that the user is new
        when(userRepository.findByUsername("john")).thenReturn(null);
        when(userRepository.findByEmail("john@example.com")).thenReturn(null);

        // simulate encoded password
        when(passwordEncoder.encode("1234")).thenReturn("encoded_1234");

        // simulates saving the user
        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setUsername("john");
        savedUser.setEmail("john@example.com");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // calls the register method
        UserResponse response = userService.register(registration);

        // asserts that the response is as expected
        assertNotNull(response);
        assertEquals("john", response.getUsername());
        assertNotNull(response.getMessage());
    }

    @Test
    void register_shouldThrowException_whenUsernameOrEmailExists() {
        UserRegistration registration = new UserRegistration("john", "1234", "john@example.com", "John", "Doe");

        // simulate that the username already exists in the database
        when(userRepository.findByUsername("john")).thenReturn(new User());

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(registration));
    }

    @Test
    void login_shouldReturnLoginResponse_whenCredentialsValid() {
        String rawPassword = "1234";
        String encodedPassword = "encoded_1234";

        // create fake user
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("john");
        user.setPassword(encodedPassword);

        // mock repository -> when findByUsername is called with "john", return the fake
        // user
        when(userRepository.findByUsername("john")).thenReturn(user);

        // simulate password match
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        LoginResponse response = userService.login(new UserLogin("john", rawPassword));

        assertEquals("john", response.getUsername());
    }

    @Test
    void login_shouldThrowException_whenInvalidCredentials() {
        when(userRepository.findByUsername("john")).thenReturn(null);

        assertThrows(InvalidCredentialsException.class, () -> userService.login(new UserLogin("john", "wrongpass")));
    }

    @Test
    void getProfileById_shouldReturnUserProfile_whenUserExists() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserProfile profile = userService.getProfileById(id);

        assertEquals("john", profile.getUsername());
        assertEquals("john@example.com", profile.getEmail());
    }

    @Test
    void getProfileById_shouldThrowException_whenUserNotFound() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserProfileNotFoundException.class, () -> userService.getProfileById(id));
    }
}
