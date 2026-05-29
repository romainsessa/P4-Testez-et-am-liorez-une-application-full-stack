package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.services.SessionService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/session")
@Log4j2
public class SessionController {
	private final SessionService sessionService;

	public SessionController(SessionService sessionService) {
		this.sessionService = sessionService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<SessionDto> findById(@PathVariable String id) {
		SessionDto session = this.sessionService.getById(Long.valueOf(id));
		return ResponseEntity.ok().body(session);
	}

	@GetMapping()
	public ResponseEntity<?> findAll() {
		List<SessionDto> sessions = this.sessionService.findAll();
		return ResponseEntity.ok().body(sessions);
	}

	@PostMapping()
	public ResponseEntity<SessionDto> create(@Valid @RequestBody SessionDto sessionDto) {
		log.info(sessionDto);
		SessionDto session = this.sessionService.create(sessionDto);
		log.info(session);
		return ResponseEntity.ok().body(session);
	}

	@PutMapping("{id}")
	public ResponseEntity<SessionDto> update(@PathVariable String id, @Valid @RequestBody SessionDto sessionDto) {
		SessionDto session = this.sessionService.update(Long.parseLong(id), sessionDto);
		return ResponseEntity.ok().body(session);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> save(@PathVariable String id) {
		this.sessionService.delete(Long.parseLong(id));
		return ResponseEntity.ok().build();
	}

	@PostMapping("{id}/participate/{userId}")
	public ResponseEntity<Void> participate(@PathVariable String id, @PathVariable String userId) {
		this.sessionService.participate(Long.parseLong(id), Long.parseLong(userId));
		return ResponseEntity.ok().build();

	}

	@DeleteMapping("{id}/participate/{userId}")
	public ResponseEntity<Void> noLongerParticipate(@PathVariable String id, @PathVariable String userId) {
		this.sessionService.noLongerParticipate(Long.parseLong(id), Long.parseLong(userId));
		return ResponseEntity.ok().build();

	}
}
