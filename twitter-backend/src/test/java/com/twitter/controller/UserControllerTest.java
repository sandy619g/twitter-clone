package com.twitter.controller;

import com.twitter.model.User;
import com.twitter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepo;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("Sandy Doe");
        user.setHandle("@johndoe");
        user.setLocation("NY");
        user.setBio("Bio here");
        user.setAvatarUrl("uploads/avatar.jpg");
    }

    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        when(userRepo.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Sandy Doe"));
    }

    @Test
    void getUserById_found() throws Exception {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Sandy Doe"));
    }

    @Test
    void getUserById_notFound() throws Exception {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_shouldSave() throws Exception {
        MockMultipartFile usernamePart = new MockMultipartFile("username", "", "text/plain", "Sandy Doe".getBytes());
        MockMultipartFile handlePart = new MockMultipartFile("handle", "", "text/plain", "@johndoe".getBytes());
        MockMultipartFile locationPart = new MockMultipartFile("location", "", "text/plain", "NY".getBytes());
        MockMultipartFile bioPart = new MockMultipartFile("bio", "", "text/plain", "A cool bio".getBytes());

        MockMultipartFile avatarFile = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", "dummy".getBytes());

        when(userRepo.save(any(User.class))).thenReturn(user);

        mockMvc.perform(multipart("/api/users")
                        .file(usernamePart)
                        .file(handlePart)
                        .file(locationPart)
                        .file(bioPart)
                        .file(avatarFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Sandy Doe"));
    }

    @Test
    void updateUser_found() throws Exception {
        User updated = new User();
        updated.setUsername("Roby");
        updated.setHandle("@roby");
        updated.setLocation("CA");
        updated.setBio("new bio");
        updated.setAvatarUrl("new.png");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(updated);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "username": "Roby",
                              "handle": "@roby",
                              "location": "CA",
                              "bio": "new bio",
                              "avatarUrl": "new.png"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Roby"));
    }

    @Test
    void updateUser_notFound() throws Exception {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "username": "Roby",
                              "handle": "@roby",
                              "location": "CA",
                              "bio": "new bio",
                              "avatarUrl": "new.png"
                            }
                        """))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_found() throws Exception {
        when(userRepo.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_notFound() throws Exception {
        when(userRepo.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNotFound());
    }
}
