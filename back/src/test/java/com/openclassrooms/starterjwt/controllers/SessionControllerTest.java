package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class SessionControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private SessionRepository sessionRepository;
	@Autowired
    private TeacherRepository teacherRepository;
	@Autowired
	private UserRepository userRepository;


	@Autowired
	private ObjectMapper objectMapper;
	
	private Long sessionId;
	private Long teacherId;
	private Long userId;

	@BeforeEach
    void setup() {
		//GIVEN
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();
        userRepository.deleteAll();
        
        User user = new User();
        user.setEmail("yoga@studio.com");
        user.setPassword("fake");
        user.setAdmin(false);
        user.setFirstName("...");
        user.setLastName("...");
        user = userRepository.save(user);
        userId = user.getId();
        
        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher = teacherRepository.save(teacher);
        teacherId = teacher.getId();
        
        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("Yoga Session");
        session.setDate(new Date());
        session.setTeacher(teacher);
        
        session = sessionRepository.save(session);
        sessionId = session.getId();
        
        sessionRepository.findAll().forEach((sess) -> { System.out.println(sess.getId() + " " + sess.getName()); });
    }	

	@Test
	void should_return_all_sessions() throws Exception {
		//WHEN / THEN
		mockMvc.perform(get("/api/session")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("Yoga Session"));
	}

	@Test
	void should_return_session_by_id() throws Exception {
		//WHEN / THEN
		mockMvc.perform(get("/api/session/" + sessionId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Yoga Session"));
	}

	@Test
	void should_create_session() throws Exception {
		//GIVEN
		SessionDto dto = new SessionDto();
		dto.setName("Yoga Session 2");
		dto.setDescription("Description");
		dto.setDate(new Date());
		dto.setTeacher_id(teacherId);

		//WHEN / THEN
		mockMvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Yoga Session 2"));
	}

	@Test
	void should_update_session() throws Exception {
		//GIVEN
		SessionDto dto = new SessionDto();		
		dto.setName("Yoga Session Update");
		dto.setDescription("Description");
		dto.setDate(new Date());
		dto.setTeacher_id(teacherId);

		//WHEN / THEN
		mockMvc.perform(put("/api/session/"+sessionId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Yoga Session Update"));
	}

	@Test
	void should_delete_session() throws Exception {
		//WHEN / THEN
		mockMvc.perform(delete("/api/session/"+ sessionId)).andExpect(status().isOk());
	}

	@Test
	void should_participate() throws Exception {
		mockMvc.perform(post("/api/session/"+ sessionId +"/participate/"+ userId)).andExpect(status().isOk());
	}

	@Test
	void should_no_longer_participate() throws Exception {
		mockMvc.perform(post("/api/session/"+ sessionId +"/participate/"+ userId)).andExpect(status().isOk());
		mockMvc.perform(delete("/api/session/" + sessionId + "/participate/"+ userId)).andExpect(status().isOk());

	}

	@Test
	void should_return_bad_request_when_session_invalid() throws Exception {
		//GIVEN
		SessionDto dto = new SessionDto();
		
		//WHEN / THEN
		mockMvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
	}

}