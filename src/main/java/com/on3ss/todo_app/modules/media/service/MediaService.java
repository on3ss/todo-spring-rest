package com.on3ss.todo_app.modules.media.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.on3ss.todo_app.infrastructure.exceptions.BusinessException;
import com.on3ss.todo_app.modules.media.domain.Attachment;
import com.on3ss.todo_app.modules.media.events.ImageUploadedEvent;
import com.on3ss.todo_app.modules.media.repository.AttachmentRepository;
import com.on3ss.todo_app.modules.todo.domain.Todo;
import com.on3ss.todo_app.modules.todo.repository.TodoRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MediaService {
    private final AttachmentRepository attachmentRepository;
    private final TodoRepository todoRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get("uploads"));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder!");
        }
    }

    @Transactional
    public void handleMultipleUploads(UUID todoUUid, MultipartFile[] files, String email) throws IOException {
        Todo todo = todoRepository.findById(todoUUid)
                .orElseThrow(() -> new BusinessException("Todo not found"));

        if (!todo.getOwner().getEmail().equals(email)) {
            throw new BusinessException("Unauthorized: You do not own this Todo.");
        }

        for (MultipartFile file : files) {
            validateFile(file);
        }

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                // We reuse the single upload logic here
                processSingleFile(todo, file);
            }
        }
    }

    private void processSingleFile(Todo todo, MultipartFile file) throws IOException {
        // 1. Physical Save
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path targetLocation = Paths.get("uploads").toAbsolutePath().resolve(fileName);
        file.transferTo(targetLocation.toFile());

        // 2. Save Metadata
        Attachment attachment = Attachment.builder()
                .fileName(file.getOriginalFilename())
                .filePath(targetLocation.toString())
                .fileType(file.getContentType())
                .todo(todo)
                .status(Attachment.ProcessingStatus.PENDING)
                .build();
        attachmentRepository.save(attachment);

        // 3. Fire Individual Event
        eventPublisher.publishEvent(new ImageUploadedEvent(
                attachment.getId(),
                attachment.getFilePath(),
                todo.getUuid()));
    }

    public void validateFile(MultipartFile file) {
        // 1. Check if empty
        if (file.isEmpty()) {
            throw new BusinessException("Cannot upload an empty file");
        }

        // 2. Check Size
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("File " + file.getOriginalFilename() + " exceeds 5MB limit");
        }

        // 3. Check Content Type
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BusinessException("Only JPEG, PNG, and WebP are allowed");
        }
    }
}
