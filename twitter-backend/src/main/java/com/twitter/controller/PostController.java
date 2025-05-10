package com.twitter.controller;

import com.twitter.model.Post;
import com.twitter.model.PostDto;
import com.twitter.repository.PostRepository;
import com.twitter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired private PostRepository postRepo;
    @Autowired private UserRepository userRepo;

    @GetMapping
    public List<PostDto> getAllPosts() {
        return postRepo.findAll().stream()
                .map(post -> new PostDto(
                        post.getId(),
                        post.getContent(),
                        post.getCreatedAt(),
                        post.getUser() != null ? post.getUser().getId() : null,
                        post.getUser() != null ? post.getUser().getUsername() : "Unknown"
                ))
                .collect(Collectors.toList());
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Post> createPost(@PathVariable Long userId, @RequestBody Map<String, String> payload) {
        return userRepo.findById(userId).map(user -> {
            Post post = new Post();
            post.setUser(user);
            post.setContent(payload.get("content"));
            postRepo.save(post);
            return ResponseEntity.ok(post);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getUserPosts(@PathVariable Long userId) {
        return userRepo.findById(userId)
                .map(user -> ResponseEntity.ok(user.getPosts()))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        if (!postRepo.existsById(id)) return ResponseEntity.notFound().build();
        postRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

