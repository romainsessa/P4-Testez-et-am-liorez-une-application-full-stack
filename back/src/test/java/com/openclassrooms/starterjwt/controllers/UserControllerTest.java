package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        user = new User();
        user.setEmail("test@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");

        userRepository.save(user);
    }

    @Test
    void should_return_user_by_id() throws Exception {
        Long id = userRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/user/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void should_return_bad_request_when_id_invalid() throws Exception {
        mockMvc.perform(get("/api/user/abc"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void should_return_not_found_when_user_not_exists() throws Exception {
        mockMvc.perform(get("/api/user/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    void should_delete_user_when_authenticated_user_matches() throws Exception {

        Long id = userRepository.findAll().get(0).getId();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        "test@test.com", null, List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(delete("/api/user/" + id))
                .andExpect(status().isOk());

        assert(userRepository.findById(id).isEmpty());
    }

    @Test
    void should_return_unauthorized_when_user_not_owner() throws Exception {

        Long id = userRepository.findAll().get(0).getId();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        "other@test.com", null, List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(delete("/api/user/" + id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_return_bad_request_when_delete_id_invalid() throws Exception {
        mockMvc.perform(delete("/api/user/abc"))
                .andExpect(status().isBadRequest());
    }
}

