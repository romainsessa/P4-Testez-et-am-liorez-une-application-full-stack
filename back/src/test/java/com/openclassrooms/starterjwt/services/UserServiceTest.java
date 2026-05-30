package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private UserMapper userMapper;

	@InjectMocks
	private UserService userService;

	private User user;
	private UserDto userDto;
	private SignupRequest signupRequest;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(1L);
		user.setEmail("test@test.com");

		userDto = new UserDto();
		userDto.setId(1L);
		userDto.setEmail("test@test.com");

		signupRequest = new SignupRequest();
		signupRequest.setEmail("test@test.com");
		signupRequest.setFirstName("John");
		signupRequest.setLastName("Doe");
		signupRequest.setPassword("password");
	}

	@Test
	void should_delete_user_by_id() {
		// WHEN
		userService.delete(1L);

		// THEN
		verify(userRepository).deleteById(1L);
	}

	@Test
	void should_return_user_dto_when_user_exists() {
		// GIVEN
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(userMapper.toDto(user)).thenReturn(userDto);

		// WHEN
		UserDto result = userService.findById(1L);

		// THEN
		assertNotNull(result);
		assertEquals("test@test.com", result.getEmail());

		verify(userRepository).findById(1L);
		verify(userMapper).toDto(user);
	}

	@Test
	void should_throw_not_found_exception_when_user_not_exists() {
		// GIVEN
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		// WHEN / THEN
		NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.findById(1L));

		assertEquals("Error : user 1 not found", exception.getMessage());

		verify(userRepository).findById(1L);
	}

	@Test
	void should_create_user_when_email_not_taken() {
		// GIVEN
		when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
		when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword");

		// WHEN
		userService.create(signupRequest);

		// THEN
		verify(userRepository).existsByEmail(signupRequest.getEmail());
		verify(passwordEncoder).encode(signupRequest.getPassword());
		verify(userRepository).save(any(User.class));
	}

	@Test
	void should_throw_exception_when_email_already_exists() {
		// GIVEN
		when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

		// WHEN / THEN
		BadRequestException exception = assertThrows(BadRequestException.class,
				() -> userService.create(signupRequest));

		assertEquals("Error: Email is already taken!", exception.getMessage());

		verify(userRepository).existsByEmail(signupRequest.getEmail());
		verify(userRepository, never()).save(any());
	}
}
