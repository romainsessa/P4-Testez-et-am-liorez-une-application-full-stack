package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

	@Mock
	private TeacherRepository teacherRepository;

	@Mock
	private TeacherMapper teacherMapper;

	@InjectMocks
	private TeacherService teacherService;

	private Teacher teacher;
	private TeacherDto teacherDto;

	@BeforeEach
	void setUp() {
		teacher = new Teacher();
		teacher.setId(1L);

		teacherDto = new TeacherDto();
		teacherDto.setId(1L);
	}

	@Test
	void should_return_all_teachers() {
		// GIVEN
		List<Teacher> teachers = List.of(teacher);
		List<TeacherDto> teacherDtos = List.of(teacherDto);

		when(teacherRepository.findAll()).thenReturn(teachers);
		when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

		// WHEN
		List<TeacherDto> result = teacherService.findAll();

		// THEN
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(1L, result.get(0).getId());

		verify(teacherRepository).findAll();
		verify(teacherMapper).toDto(teachers);
	}

	@Test
	void should_return_teacher_when_exists() {
		// GIVEN
		when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
		when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

		// WHEN
		TeacherDto result = teacherService.findById(1L);

		// THEN
		assertNotNull(result);
		assertEquals(1L, result.getId());

		verify(teacherRepository).findById(1L);
		verify(teacherMapper).toDto(teacher);
	}

	@Test
	void should_throw_not_found_exception_when_teacher_not_found() {
		// GIVEN
		when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

		// WHEN / THEN
		NotFoundException exception = assertThrows(NotFoundException.class, () -> teacherService.findById(1L));

		assertEquals("Error : teacher 1 not found", exception.getMessage());

		verify(teacherRepository).findById(1L);
	}
}
