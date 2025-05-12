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


    private MockMultipartFile textPart(String name, String value) {
        return new MockMultipartFile(name, "", "text/plain", value.getBytes());
    }

    private MockMultipartFile avatarPart(String filename, String content) {
        return new MockMultipartFile("avatar", filename, "image/jpeg", content.getBytes());
    }

    private MockMultipartFile[] defaultUserParts(boolean includeAvatar) {
        return includeAvatar ?
                new MockMultipartFile[]{
                        textPart("username", "Sandy Doe"),
                        textPart("handle", "@sandy"),
                        textPart("location", "NY"),
                        textPart("bio", "A cool bio"),
                        avatarPart("avatar.jpg", "image-data")
                }
                :
                new MockMultipartFile[]{
                        textPart("username", "Sandy Doe"),
                        textPart("handle", "@sandy"),
                        textPart("location", "NY"),
                        textPart("bio", "A cool bio")
                };
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
    void createUser_withAvatar_shouldSave() throws Exception {
        when(userRepo.save(any(User.class))).thenReturn(user);

        mockMvc.perform(multipart("/api/users")
                        .file(defaultUserParts(true)[0])
                        .file(defaultUserParts(true)[1])
                        .file(defaultUserParts(true)[2])
                        .file(defaultUserParts(true)[3])
                        .file(defaultUserParts(true)[4])
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Sandy Doe"));
    }

    @Test
    void createUser_withoutAvatar_shouldSave() throws Exception {
        when(userRepo.save(any(User.class))).thenReturn(user);

        mockMvc.perform(multipart("/api/users")
                        .file(defaultUserParts(false)[0])
                        .file(defaultUserParts(false)[1])
                        .file(defaultUserParts(false)[2])
                        .file(defaultUserParts(false)[3])
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Sandy Doe"));
    }

    @Test
    void createUser_shouldFailOnAvatarUpload() throws Exception {
        when(fileStorageService.saveFile(any(MultipartFile.class)))
                .thenThrow(new IOException("IO error"));

        mockMvc.perform(multipart("/api/users")
                        .file(defaultUserParts(true)[0])
                        .file(defaultUserParts(true)[1])
                        .file(defaultUserParts(true)[2])
                        .file(defaultUserParts(true)[3])
                        .file(defaultUserParts(true)[4])
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void updateUser_withAvatar_shouldUpdateUser() throws Exception {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(multipart("/api/users/1")
                        .file(defaultUserParts(true)[0])
                        .file(defaultUserParts(true)[1])
                        .file(defaultUserParts(true)[2])
                        .file(defaultUserParts(true)[3])
                        .file(defaultUserParts(true)[4])
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Sandy Doe"));
    }

    @Test
    void updateUser_withoutAvatar_shouldUpdateUser() throws Exception {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(multipart("/api/users/1")
                        .file(defaultUserParts(false)[0])
                        .file(defaultUserParts(false)[1])
                        .file(defaultUserParts(false)[2])
                        .file(defaultUserParts(false)[3])
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Sandy Doe"));
    }

    @Test
    void updateUser_userNotFound_shouldReturn404() throws Exception {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(multipart("/api/users/1")
                        .file(defaultUserParts(false)[0])
                        .file(defaultUserParts(false)[1])
                        .file(defaultUserParts(false)[2])
                        .file(defaultUserParts(false)[3])
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_shouldFailOnAvatarUpload() throws Exception {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(fileStorageService.saveFile(any(MultipartFile.class)))
                .thenThrow(new IOException("Simulated IO Error"));

        mockMvc.perform(multipart("/api/users/1")
                        .file(defaultUserParts(true)[0])
                        .file(defaultUserParts(true)[1])
                        .file(defaultUserParts(true)[2])
                        .file(defaultUserParts(true)[3])
                        .file(defaultUserParts(true)[4])
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());
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
    void createUser_withEmptyAvatar_shouldNotSaveAvatar() throws Exception {
        MockMultipartFile emptyAvatar = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", new byte[0]);

        when(userRepo.save(any(User.class))).thenReturn(user);

        mockMvc.perform(multipart("/api/users")
                        .file(textPart("username", "Sandy Doe"))
                        .file(textPart("handle", "@sandy"))
                        .file(textPart("location", "NY"))
                        .file(textPart("bio", "Bio"))
                        .file(emptyAvatar)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Sandy Doe"));
    }

    @Test
    void updateUser_withEmptyAvatar_shouldNotUpdateAvatar() throws Exception {
        MockMultipartFile emptyAvatar = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", new byte[0]);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(multipart("/api/users/1")
                        .file(textPart("username", "Sandy Doe"))
                        .file(textPart("handle", "@sandy"))
                        .file(textPart("location", "NY"))
                        .file(textPart("bio", "Bio"))
                        .file(emptyAvatar)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Sandy Doe"));
    }


}
