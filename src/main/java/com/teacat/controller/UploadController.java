package com.teacat.controller;

import com.teacat.service.AuthService;
import com.teacat.service.ImageStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class UploadController {
    private final AuthService authService;
    private final ImageStorageService imageStorageService;

    public UploadController(AuthService authService, ImageStorageService imageStorageService) {
        this.authService = authService;
        this.imageStorageService = imageStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file,
                                               @RequestHeader("Authorization") String authHeader) throws IOException {
        authService.requireUser(authHeader);
        try {
            return ResponseEntity.ok(imageStorageService.store(file));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
