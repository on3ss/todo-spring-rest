package com.on3ss.todo_app.modules.media.controller;

import com.on3ss.todo_app.modules.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/todos/{todoUUid}/images")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadImage(
            @PathVariable UUID todoUUid,
            @RequestParam MultipartFile file,
            Principal principal) {

        try {
            mediaService.handleUpload(todoUUid, file, principal.getName());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Image upload initiated. Thumbnail is being processed in the background.");
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(response);

        } catch (IOException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to store file " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
}