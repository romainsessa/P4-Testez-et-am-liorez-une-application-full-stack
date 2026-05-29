package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SessionService {
	private final SessionRepository sessionRepository;
	private final UserRepository userRepository;
	private final SessionMapper sessionMapper;

	public SessionService(SessionRepository sessionRepository, UserRepository userRepository,
			SessionMapper sessionMapper) {
		this.sessionRepository = sessionRepository;
		this.userRepository = userRepository;
		this.sessionMapper = sessionMapper;
	}

	public SessionDto create(SessionDto sessionDto) {
		Session session = this.sessionRepository.save(this.sessionMapper.toEntity(sessionDto));
		return this.sessionMapper.toDto(session);
	}

	public void delete(Long id) {
		Optional<Session> session = this.sessionRepository.findById(id);
		if (session.isEmpty()) {
			throw new NotFoundException("Session " + id + " not found");
		}
		this.sessionRepository.deleteById(id);
	}

	public List<SessionDto> findAll() {
		List<Session> sessions = this.sessionRepository.findAll();
		return this.sessionMapper.toDto(sessions);
	}

	public SessionDto getById(Long id) {
		Optional<Session> session = this.sessionRepository.findById(id);
		if (session.isEmpty()) {
			throw new NotFoundException("Session " + id + " not found");
		}
		return this.sessionMapper.toDto(session.get());
	}

	public SessionDto update(Long id, SessionDto sessionDto) {
		sessionDto.setId(id);
		Session session = this.sessionRepository.save(this.sessionMapper.toEntity(sessionDto));
		return this.sessionMapper.toDto(session);
	}

	public void participate(Long id, Long userId) {
		Session session = this.sessionRepository.findById(id).orElse(null);
		User user = this.userRepository.findById(userId).orElse(null);
		if (session == null || user == null) {
			throw new NotFoundException("Error: session " + id + " or user " + userId + " not found");
		}
		boolean alreadyParticipate = session.getUsers().stream().anyMatch(o -> o.getId().equals(userId));
		if (alreadyParticipate) {
			throw new BadRequestException("Error: already participate");
		}
		session.getUsers().add(user);
		this.sessionRepository.save(session);
	}

	public void noLongerParticipate(Long id, Long userId) {
		Session session = this.sessionRepository.findById(id).orElse(null);
		User user = this.userRepository.findById(userId).orElse(null);
		if (session == null || user == null) {
			throw new NotFoundException("Error: session " + id + " or user " + userId + " not found");
		}
		boolean alreadyParticipate = session.getUsers().stream().anyMatch(o -> o.getId().equals(userId));
		if (!alreadyParticipate) {
			throw new BadRequestException("Error: don't participate");
		}
		session.setUsers(
				session.getUsers().stream().filter(u -> !u.getId().equals(userId)).collect(Collectors.toList()));
		this.sessionRepository.save(session);
	}
}
