package com.twitter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.model.Post;
import com.twitter.model.User;
import com.twitter.repository.PostRepository;
import com.twitter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
public class PostControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean
    private PostRepository postRepo;
    @MockitoBean
    private UserRepository userRepo;

    private Post post;
    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("john");

        post = new Post();
        post.setId(1L);
        post.setContent("Hello world!");
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getAllPosts_shouldReturnList() throws Exception {
        when(postRepo.findAll()).thenReturn(List.of(post));

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hello world!"));
    }

    @Test
    void createPost_shouldSave() throws Exception {
        Map<String, String> payload = Map.of("content", "Test post");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(postRepo.save(any(Post.class))).thenReturn(post);

        mockMvc.perform(post("/api/posts/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Test post"));
    }

    @Test
    void createPost_userNotFound() throws Exception {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/posts/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Test\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserPosts_found() throws Exception {
        user.setPosts(List.of(post));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/posts/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hello world!"));
    }

    @Test
    void getUserPosts_notFound() throws Exception {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/posts/user/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPosts_postWithoutUser_shouldHandleGracefully() throws Exception {
        Post postWithoutUser = new Post();
        postWithoutUser.setId(2L);
        postWithoutUser.setContent("Orphan post");
        postWithoutUser.setCreatedAt(LocalDateTime.now());
        postWithoutUser.setUser(null); // Important part

        when(postRepo.findAll()).thenReturn(List.of(postWithoutUser));

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Orphan post"))
                .andExpect(jsonPath("$[0].userId").doesNotExist())
                .andExpect(jsonPath("$[0].username").value("Unknown"));
    }

    @Test
    void handleAllExceptions_shouldReturnInternalServerError() throws Exception {
        when(postRepo.findAll()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Database error"));
    }
}

