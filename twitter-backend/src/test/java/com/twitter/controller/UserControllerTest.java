package com.twitter.controller;

import com.twitter.model.User;
import com.twitter.repository.UserRepository;
import com.twitter.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @MockitoBean
    private FileStorageService fileStorageService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("Sandy Doe");
        user.setHandle("@sandy");
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
        MockMultipartFile handlePart = new MockMultipartFile("handle", "", "text/plain", "@sandy".getBytes());
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
    void updateUser_withMultipart_shouldUpdateUser() throws Exception {
        MockMultipartFile usernamePart = new MockMultipartFile("username", "", "text/plain", "Updated User".getBytes());
        MockMultipartFile handlePart = new MockMultipartFile("handle", "", "text/plain", "@updated".getBytes());
        MockMultipartFile locationPart = new MockMultipartFile("location", "", "text/plain", "San Francisco".getBytes());
        MockMultipartFile bioPart = new MockMultipartFile("bio", "", "text/plain", "Updated bio".getBytes());
        MockMultipartFile avatarFile = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", "image-data".getBytes());

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(multipart("/api/users/1")
                        .file(usernamePart)
                        .file(handlePart)
                        .file(locationPart)
                        .file(bioPart)
                        .file(avatarFile)
                        .with(request -> {
                            request.setMethod("PUT"); // Mock PUT method
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Updated User"))
                .andExpect(jsonPath("$.handle").value("@updated"))
                .andExpect(jsonPath("$.location").value("San Francisco"))
                .andExpect(jsonPath("$.bio").value("Updated bio"));
    }


    @Test
    void updateUser_withMultipart_userNotFound_shouldReturn404() throws Exception {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        MockMultipartFile usernamePart = new MockMultipartFile("username", "", "text/plain", "New Name".getBytes());
        MockMultipartFile handlePart = new MockMultipartFile("handle", "", "text/plain", "@new".getBytes());
        MockMultipartFile locationPart = new MockMultipartFile("location", "", "text/plain", "New York".getBytes());
        MockMultipartFile bioPart = new MockMultipartFile("bio", "", "text/plain", "New bio".getBytes());

        mockMvc.perform(multipart("/api/users/1")
                        .file(usernamePart)
                        .file(handlePart)
                        .file(locationPart)
                        .file(bioPart)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
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

    @Test
    void createUser_shouldFailOnAvatarUpload() throws Exception {
        MockMultipartFile usernamePart = new MockMultipartFile("username", "", "text/plain", "Sandy Doe".getBytes());
        MockMultipartFile handlePart = new MockMultipartFile("handle", "", "text/plain", "@sandy".getBytes());
        MockMultipartFile locationPart = new MockMultipartFile("location", "", "text/plain", "NY".getBytes());
        MockMultipartFile bioPart = new MockMultipartFile("bio", "", "text/plain", "A cool bio".getBytes());
        MockMultipartFile avatarFile = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", "fake".getBytes());

        when(fileStorageService.saveFile(any(MultipartFile.class)))
                .thenThrow(new IOException("IO error"));

        mockMvc.perform(multipart("/api/users")
                        .file(usernamePart)
                        .file(handlePart)
                        .file(locationPart)
                        .file(bioPart)
                        .file(avatarFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateUser_shouldFailOnAvatarUpload() throws Exception {
        MockMultipartFile usernamePart = new MockMultipartFile("username", "", "text/plain", "Updated User".getBytes());
        MockMultipartFile handlePart = new MockMultipartFile("handle", "", "text/plain", "@updated".getBytes());
        MockMultipartFile locationPart = new MockMultipartFile("location", "", "text/plain", "SF".getBytes());
        MockMultipartFile bioPart = new MockMultipartFile("bio", "", "text/plain", "Bio".getBytes());
        MockMultipartFile avatarFile = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", "image".getBytes());

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        when(fileStorageService.saveFile(any(MultipartFile.class)))
                .thenThrow(new IOException("Simulated IO Error"));

        mockMvc.perform(multipart("/api/users/1")
                        .file(usernamePart)
                        .file(handlePart)
                        .file(locationPart)
                        .file(bioPart)
                        .file(avatarFile)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());
    }
}
