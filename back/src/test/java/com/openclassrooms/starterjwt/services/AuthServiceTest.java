package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("password");

        userDetails = new UserDetailsImpl(
                1L,
                "test@test.com",
                "John",
                "Doe",
                true,
                "password"
        );
    }


    @Test
    void should_authenticate_and_return_jwt_response_with_admin() {
        // GIVEN
        User user = new User();
        user.setEmail("test@test.com");
        user.setAdmin(true);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(jwtUtils.generateJwtToken(authentication)).thenReturn("fake-jwt");

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        // WHEN
        JwtResponse response = authService.authenticate(loginRequest);

        // THEN
        assertNotNull(response);
        assertEquals("fake-jwt", response.getToken());
        assertEquals(1L, response.getId());
        assertEquals("test@test.com", response.getUsername());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertTrue(response.getAdmin());

        verify(authenticationManager).authenticate(any());
        verify(jwtUtils).generateJwtToken(authentication);
        verify(userRepository).findByEmail("test@test.com");
    }

    @Test
    void should_authenticate_and_return_non_admin_when_user_not_found() {
        // GIVEN
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(jwtUtils.generateJwtToken(authentication)).thenReturn("fake-jwt");

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());

        // WHEN
        JwtResponse response = authService.authenticate(loginRequest);

        // THEN
        assertNotNull(response);
        assertFalse(response.getAdmin());

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByEmail("test@test.com");
    }
}
