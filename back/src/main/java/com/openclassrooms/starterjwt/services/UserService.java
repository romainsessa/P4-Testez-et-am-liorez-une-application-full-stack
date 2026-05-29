package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public void delete(Long id) {
        this.userRepository.deleteById(id);
    }

    public UserDto findById(Long id) {
        return this.userMapper.toDto(this.userRepository.findById(id).orElse(null));
    }
    
    public void create(SignupRequest signUpRequest) {
    	if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Error: Email is already taken!");
        }

        // Create new user's account
        User user = new User(signUpRequest.getEmail(),
                signUpRequest.getLastName(),
                signUpRequest.getFirstName(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                false);

        userRepository.save(user);
    }
}
