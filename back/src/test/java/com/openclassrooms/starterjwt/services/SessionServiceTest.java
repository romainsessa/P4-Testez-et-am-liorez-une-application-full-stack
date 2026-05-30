package com.openclassrooms.starterjwt.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

	@Mock
	private SessionRepository sessionRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private SessionMapper sessionMapper;
	@InjectMocks
	private SessionService sessionService;

	@Test
	void should_return_all_sessions() {
		// GIVEN
		Session session = new Session();
		Long id = 1L;
		session.setId(id);
		SessionDto sessionDto = new SessionDto();
		sessionDto.setId(id);

		when(sessionRepository.findAll()).thenReturn(List.of(session));
		when(sessionMapper.toDto(List.of(session))).thenReturn(List.of(sessionDto));

		// WHEN
		List<SessionDto> result = sessionService.findAll();
		Long obtainedId = result.get(0).getId();

		// THEN
		assertEquals(1, result.size());
		assertEquals(id, obtainedId);
		verify(sessionRepository).findAll();
	}

	@Test
	void should_return_session_by_id() {
		// GIVEN
		Long id = 1L;
		Session session = new Session();
		session.setId(id);
		SessionDto sessionDto = new SessionDto();
		sessionDto.setId(id);

		when(sessionRepository.findById(id)).thenReturn(Optional.of(session));
		when(sessionMapper.toDto(session)).thenReturn(sessionDto);

		// WHEN
		SessionDto result = sessionService.getById(id);

		// THEN
		assertNotNull(result);
		assertEquals(id, result.getId());
	}

	@Test
	void should_return_exception_when_session_not_found() {
		// GIVEN
		when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

		// WHEN / THEN
		assertThrows(NotFoundException.class, () -> {
			sessionService.getById(1L);
		});
	}

	@Test
	void should_save_session() {
		// GIVEN
		Session session = new Session();
		session.setName("Test");

		SessionDto sessionDto = new SessionDto();
		Long id = 1L;
		sessionDto.setId(id);
		sessionDto.setName("Test");

		when(sessionRepository.save(session)).thenReturn(session);
		when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
		when(sessionMapper.toDto(session)).thenReturn(sessionDto);

		// WHEN
		SessionDto result = sessionService.create(sessionDto);

		// THEN
		assertNotNull(result);
		assertEquals("Test", result.getName());
		verify(sessionRepository).save(session);
	}

	@Test
	void should_delete_session() {
		//GIVEN
		Long id = 1L;
		Session session = new Session();
		session.setId(id);
		when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

		// WHEN
		sessionService.delete(1L);

		// THEN
		verify(sessionRepository).deleteById(1L);
	}
	
	@Test
	void should_return_exception_when_session_not_found_during_delete() {
		// GIVEN
		when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

		// WHEN / THEN
		assertThrows(NotFoundException.class, () -> {
			sessionService.delete(1L);
		});
	}
	
	@Test
	void should_update_session() {
		// GIVEN
		Long id = 1L;
		Session session = new Session();
		session.setId(id);
		session.setName("Test");

		SessionDto sessionDto = new SessionDto();		
		sessionDto.setName("Test");

		when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
		when(sessionRepository.save(session)).thenReturn(session);
		when(sessionMapper.toDto(session)).thenReturn(sessionDto);

		// WHEN
		SessionDto result = sessionService.update(id, sessionDto);

		// THEN
		assertNotNull(result);
		assertEquals("Test", result.getName());
		verify(sessionRepository).save(session);
	}
	
	@Test
	void should_participate_return_exception() {
		//GIVEN
		Long id = 1L;
		Long userId = 1L;
	
		User user = new User();
		user.setId(userId);
		
		Session session = new Session();
		session.setId(id);
		session.setUsers(List.of(user));
		
		when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		//WHEN / THEN
		assertThrows(BadRequestException.class, () -> {
			sessionService.participate(id, userId);
		});		
	}
	
	@Test
	void should_participate() {
		//GIVEN
		Long id = 1L;
		Long userId = 1L;
		
		User user = new User();
		user.setId(userId);
		
		Session session = new Session();
		session.setId(id);
		session.setUsers(new ArrayList<User>());
		
		when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		when(sessionRepository.save(session)).thenReturn(session);
		
		//WHEN
		sessionService.participate(id, userId);
		
		//THEN
		assertTrue(session.getUsers().contains(user));
		verify(sessionRepository).save(session);
	}
	
	@Test
	void should_no_longer_participate() {
		//GIVEN
		Long id = 1L;
		Long userId = 1L;

		User user = new User();
		user.setId(userId);
		
		Session session = new Session();
		session.setId(id);
		session.setUsers(List.of(user));
		
		when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		when(sessionRepository.save(session)).thenReturn(session);
		//WHEN
		sessionService.noLongerParticipate(id, userId);
		//THEN
		assertFalse(session.getUsers().contains(user));
		verify(sessionRepository).save(session);
	}
	
	@Test
	void should_no_longer_participate_return_exception() {
		//GIVEN
		Long id = 1L;
		Long userId = 1L;
	
		User user = new User();
		user.setId(userId);
		
		Session session = new Session();
		session.setId(id);
		session.setUsers(new ArrayList<User>());
		
		when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		//WHEN / THEN
		assertThrows(BadRequestException.class, () -> {
			sessionService.noLongerParticipate(id, userId);
		});		
	}

}
