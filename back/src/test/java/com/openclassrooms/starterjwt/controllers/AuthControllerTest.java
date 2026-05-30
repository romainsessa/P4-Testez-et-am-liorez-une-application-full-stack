package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.services.AuthService;
import com.openclassrooms.starterjwt.services.UserService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AuthService authService;

	@MockitoBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void should_login_and_return_jwt() throws Exception {

		// GIVEN
		LoginRequest request = new LoginRequest();
		request.setEmail("test@test.com");
		request.setPassword("password");

		JwtResponse jwtResponse = new JwtResponse("fake-jwt", 1L, "test@test.com", "John", "Doe", true);

		when(authService.authenticate(any(LoginRequest.class))).thenReturn(jwtResponse);

		// WHEN / THEN
		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value("fake-jwt"))
				.andExpect(jsonPath("$.username").value("test@test.com"))
				.andExpect(jsonPath("$.firstName").value("John")).andExpect(jsonPath("$.lastName").value("Doe"))
				.andExpect(jsonPath("$.admin").value(true));

		verify(authService).authenticate(any(LoginRequest.class));
	}

	@Test
	void should_register_user() throws Exception {

		// GIVEN
		SignupRequest request = new SignupRequest();
		request.setEmail("test@test.com");
		request.setPassword("password");
		request.setFirstName("John");
		request.setLastName("Doe");

		doNothing().when(userService).create(any(SignupRequest.class));

		// WHEN / THEN
		mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("User registered successfully!"));

		verify(userService).create(any(SignupRequest.class));
	}

	@Test
	void should_return_bad_request_when_login_invalid() throws Exception {

		// email manquant
		LoginRequest request = new LoginRequest();
		request.setPassword("password");

		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
	}

	@Test
	void should_return_bad_request_when_register_invalid() throws Exception {

		SignupRequest request = new SignupRequest(); // vide

		mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
	}
}
