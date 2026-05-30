package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");
    }

    @Test
    void should_return_user_details_when_user_exists() {

        // GIVEN
        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        // WHEN
        UserDetails result = userDetailsService.loadUserByUsername("test@test.com");

        // THEN
        assertNotNull(result);
        assertEquals("test@test.com", result.getUsername());
        assertEquals("password", result.getPassword());

        verify(userRepository).findByEmail("test@test.com");
    }

    @Test
    void should_throw_exception_when_user_not_found() {

        // GIVEN
        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());

        // WHEN / THEN
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("test@test.com")
        );

        assertEquals("User Not Found with email: test@test.com", exception.getMessage());

        verify(userRepository).findByEmail("test@test.com");
    }
}