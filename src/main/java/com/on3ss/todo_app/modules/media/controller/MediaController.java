package com.on3ss.todo_app.modules.media.controller;

import com.on3ss.todo_app.modules.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/todos/{todoUUid}/images")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HashMap<String, String>> uploadMultiple(
            @PathVariable UUID todoUUid,
            @RequestParam MultipartFile[] files,
            Principal principal) throws IOException {

        // Check if files were actually sent
        if (files == null || files.length == 0) {
            HashMap<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "No files selected for upload.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        mediaService.handleMultipleUploads(todoUUid, files, principal.getName());

        HashMap<String, String> response = new HashMap<>();
        response.put("message", String.format("Successfully queued %d images for processing.", files.length));
        response.put("todoId", todoUUid.toString());

        return ResponseEntity.accepted()
                .body(response);
    }
}