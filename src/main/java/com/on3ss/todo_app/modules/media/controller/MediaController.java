package com.on3ss.todo_app.modules.media.controller;

import com.on3ss.todo_app.modules.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/todos/{todoUUid}/images")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadMultiple(
            @PathVariable UUID todoUUid,
            @RequestParam MultipartFile[] files,
            Principal principal) throws IOException {

        // Check if files were actually sent
        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body("No files selected for upload.");
        }

        mediaService.handleMultipleUploads(todoUUid, files, principal.getName());

        return ResponseEntity.accepted()
                .body(String.format("Successfully queued %d images for processing.", files.length));
    }
}