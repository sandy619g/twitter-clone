package com.twitter.controller;

import com.twitter.model.User;
import com.twitter.repository.UserRepository;
import com.twitter.exception.ResourceNotFoundException;

import com.twitter.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    FileStorageService fileStorageService;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> createUser(
            @RequestPart("username") String username,
            @RequestPart("handle") String handle,
            @RequestPart("location") String location,
            @RequestPart("bio") String bio,
            @RequestPart(value = "avatar", required = false) MultipartFile avatarFile
    ) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setHandle(handle);
            user.setLocation(location);
            user.setBio(bio);

            if (avatarFile != null && !avatarFile.isEmpty()) {
                user.setAvatarUrl(fileStorageService.saveFile(avatarFile));
            }

            return ResponseEntity.ok(userRepo.save(user));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload avatar", e);
        }
    }


    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestPart("username") String username,
            @RequestPart("handle") String handle,
            @RequestPart("location") String location,
            @RequestPart("bio") String bio,
            @RequestPart(value = "avatar", required = false) MultipartFile avatarFile
    ) {
        return userRepo.findById(id).map(user -> {
            user.setUsername(username);
            user.setHandle(handle);
            user.setLocation(location);
            user.setBio(bio);

            if (avatarFile != null && !avatarFile.isEmpty()) {
                try {
                    user.setAvatarUrl(fileStorageService.saveFile(avatarFile));
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload avatar", e);
                }
            }

            return ResponseEntity.ok(userRepo.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Deleting user with id {}", id);
        if (!userRepo.existsById(id)) throw new ResourceNotFoundException("User not found with id " + id);
        userRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
