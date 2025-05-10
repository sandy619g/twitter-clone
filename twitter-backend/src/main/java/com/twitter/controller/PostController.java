package com.twitter.controller;

import com.twitter.model.Post;
import com.twitter.model.PostDto;
import com.twitter.repository.PostRepository;
import com.twitter.repository.UserRepository;
import com.twitter.exception.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private UserRepository userRepo;

    @GetMapping
    public List<PostDto> getAllPosts() {
        logger.info("Fetching all posts");
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
        logger.info("Creating post for user {}", userId);
        return userRepo.findById(userId).map(user -> {
            Post post = new Post();
            post.setUser(user);
            post.setContent(payload.get("content"));
            postRepo.save(post);
            return ResponseEntity.ok(post);
        }).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getUserPosts(@PathVariable Long userId) {
        logger.info("Fetching posts for user {}", userId);
        return userRepo.findById(userId)
                .map(user -> ResponseEntity.ok(user.getPosts()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        logger.info("Deleting post with id {}", id);
        if (!postRepo.existsById(id)) throw new ResourceNotFoundException("Post not found with id " + id);
        postRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
