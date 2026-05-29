package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {

	private final TeacherMapper teacherMapper;
	private final TeacherRepository teacherRepository;

	public TeacherService(TeacherRepository teacherRepository, TeacherMapper teacherMapper) {
		this.teacherRepository = teacherRepository;
		this.teacherMapper = teacherMapper;
	}

	public List<TeacherDto> findAll() {
		return this.teacherMapper.toDto(this.teacherRepository.findAll());
	}

	public TeacherDto findById(Long id) {
		Teacher teacher = teacherRepository.findById(id).orElse(null);
		if (teacher == null) {
			throw new NotFoundException("Error : teacher " + id + " not found");
		}
		return this.teacherMapper.toDto(teacher);
	}
}
