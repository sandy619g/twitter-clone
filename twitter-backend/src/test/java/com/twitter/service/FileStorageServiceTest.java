package com.twitter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService();
    }

    @Test
    void saveFile_shouldSaveSuccessfully() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile(
                "avatar", "avatar.jpg", "image/jpeg", "test-content".getBytes());

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            Path dummyPath = Paths.get("uploads/dummy.jpg");

            filesMock.when(() -> Files.createDirectories(any(Path.class))).thenReturn(dummyPath);
            filesMock.when(() -> Files.write(any(Path.class), any(byte[].class))).thenReturn(dummyPath);

            String result = fileStorageService.saveFile(mockFile);

            assertTrue(result.startsWith("uploads/"));
            assertTrue(result.endsWith("avatar.jpg"));
            filesMock.verify(() -> Files.createDirectories(any(Path.class)));
            filesMock.verify(() -> Files.write(any(Path.class), eq("test-content".getBytes())));
        }
    }

    @Test
    void saveFile_shouldThrowIOException() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "avatar", "avatar.jpg", "image/jpeg", "data".getBytes());

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.createDirectories(any(Path.class))).thenThrow(new IOException("Failed to create"));

            assertThrows(IOException.class, () -> fileStorageService.saveFile(mockFile));
        }
    }
}

