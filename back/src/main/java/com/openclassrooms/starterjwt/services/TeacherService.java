package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
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
        return this.teacherMapper.toDto(teacherRepository.findById(id).orElse(null));
    }
}
