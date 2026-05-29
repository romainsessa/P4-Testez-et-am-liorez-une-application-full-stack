package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable String id) {
        try {
            UserDto userDto = this.userService.findById(Long.valueOf(id));

            if (userDto == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok().body(userDto);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> save(@PathVariable String id) {
        try {
        	UserDto userDto = this.userService.findById(Long.valueOf(id));

            if (userDto == null) {
                return ResponseEntity.notFound().build();
            }

            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (!Objects.equals(userDetails.getUsername(), userDto.getEmail())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            this.userService.delete(Long.parseLong(id));
            return ResponseEntity.ok().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
