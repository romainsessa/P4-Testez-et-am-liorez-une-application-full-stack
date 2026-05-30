package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class SessionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private SessionService sessionService;

	@Autowired
	private ObjectMapper objectMapper;

	private SessionDto createSessionDto() {
		SessionDto dto = new SessionDto();
		dto.setId(1L);
		dto.setName("Yoga Session");
		dto.setDescription("Relax");
		dto.setDate(new Date());
		dto.setTeacher_id(1L);
		return dto;
	}

	@Test
	void should_return_all_sessions() throws Exception {

		List<SessionDto> sessions = List.of(createSessionDto());

		when(sessionService.findAll()).thenReturn(sessions);

		mockMvc.perform(get("/api/session")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("Yoga Session"));
	}

	@Test
	void should_return_session_by_id() throws Exception {

		SessionDto dto = createSessionDto();

		when(sessionService.getById(1L)).thenReturn(dto);

		mockMvc.perform(get("/api/session/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Yoga Session"));
	}

	@Test
	void should_create_session() throws Exception {

		SessionDto dto = createSessionDto();

		when(sessionService.create(any(SessionDto.class))).thenReturn(dto);

		mockMvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Yoga Session"));

		verify(sessionService).create(any(SessionDto.class));
	}

	@Test
	void should_update_session() throws Exception {

		SessionDto dto = createSessionDto();

		when(sessionService.update(eq(1L), any(SessionDto.class))).thenReturn(dto);

		mockMvc.perform(put("/api/session/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Yoga Session"));

		verify(sessionService).update(eq(1L), any(SessionDto.class));
	}

	@Test
	void should_delete_session() throws Exception {

		doNothing().when(sessionService).delete(1L);

		mockMvc.perform(delete("/api/session/1")).andExpect(status().isOk());

		verify(sessionService).delete(1L);
	}

	@Test
	void should_participate() throws Exception {

		doNothing().when(sessionService).participate(1L, 2L);

		mockMvc.perform(post("/api/session/1/participate/2")).andExpect(status().isOk());

		verify(sessionService).participate(1L, 2L);
	}

	@Test
	void should_no_longer_participate() throws Exception {

		doNothing().when(sessionService).noLongerParticipate(1L, 2L);

		mockMvc.perform(delete("/api/session/1/participate/2")).andExpect(status().isOk());

		verify(sessionService).noLongerParticipate(1L, 2L);
	}

	@Test
	void should_return_bad_request_when_session_invalid() throws Exception {

		SessionDto dto = new SessionDto(); // vide

		mockMvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
	}

}