package com.twitter.controller;

import com.twitter.model.User;
import com.twitter.repository.UserRepository;
import com.twitter.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepo;

    @GetMapping
    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        return userRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info("Fetching user with id {}", id);
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        logger.info("Creating user: {}", user.getUsername());
        return userRepo.save(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        logger.info("Updating user with id {}", id);
        return userRepo.findById(id).map(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setAvatarUrl(updatedUser.getAvatarUrl());
            return ResponseEntity.ok(userRepo.save(user));
        }).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Deleting user with id {}", id);
        if (!userRepo.existsById(id)) throw new ResourceNotFoundException("User not found with id " + id);
        userRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
