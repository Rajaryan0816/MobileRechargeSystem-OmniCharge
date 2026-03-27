package com.example.demo.service;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.RegisterRequestDTO;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDTO();
        registerRequest.setEmail("test@example.com");
        registerRequest.setUserName("testuser");
        registerRequest.setPhoneNumber("1234567890");
        registerRequest.setPassword("password");

        loginRequest = new LoginRequestDTO();
        loginRequest.setIdentifier("testuser");
        loginRequest.setPassword("password");

        user = new User();
        user.setId(1L);
        user.setUserName("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole("USER");
    }

    @Test
    void testRegister_Success() {
        when(repo.findByEmail(anyString())).thenReturn(Optional.empty());
        when(repo.findByUserName(anyString())).thenReturn(Optional.empty());
        when(repo.findByPhoneNumber(anyString())).thenReturn(Optional.empty());

        authService.register(registerRequest);

        verify(repo, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_EmailExists() {
        when(repo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(repo, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        when(repo.findByEmailOrUserName(anyString(), anyString())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong())).thenReturn("mock-token");

        String token = authService.login(loginRequest);

        assertNotNull(token);
        assertEquals("mock-token", token);
    }

    @Test
    void testLogin_InvalidPassword() {
        when(repo.findByEmailOrUserName(anyString(), anyString())).thenReturn(Optional.of(user));
        loginRequest.setPassword("wrong-password");

        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
    }

    @Test
    void testLogin_UserNotFound() {
        when(repo.findByEmailOrUserName(anyString(), anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
    }
}
