package com.on3ss.todo_app.modules.media.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.on3ss.todo_app.infrastructure.exceptions.BusinessException;
import com.on3ss.todo_app.modules.media.domain.Attachment;
import com.on3ss.todo_app.modules.media.domain.Attachment.ProcessingStatus;
import com.on3ss.todo_app.modules.media.events.ImageUploadedEvent;
import com.on3ss.todo_app.modules.media.repository.AttachmentRepository;
import com.on3ss.todo_app.modules.todo.domain.Todo;
import com.on3ss.todo_app.modules.todo.repository.TodoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MediaService {
    private final AttachmentRepository attachmentRepository;
    private final TodoRepository todoRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void handleUpload(UUID todoUuid, MultipartFile file, String currentUserEmail) throws IOException {
        // 1. Fetch Todo and Verify Ownership
        Todo todo = todoRepository.findById(todoUuid)
                .orElseThrow(() -> new BusinessException("Todo not found"));

        if (!todo.getOwner().getEmail().equals(currentUserEmail)) {
            throw new BusinessException("Unauthorized: You do not own this Todo.");
        }

        // 2. Ensure the 'uploads' directory exists
        Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 3. Prepare unique file path
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path targetLocation = uploadPath.resolve(fileName);

        // 4. Physical Save
        file.transferTo(targetLocation.toFile());

        // 5. Save Metadata (Set to PENDING since job hasn't finished)
        Attachment attachment = Attachment.builder()
                .fileName(file.getOriginalFilename())
                .filePath(targetLocation.toString())
                .fileType(file.getContentType())
                .todo(todo)
                .status(ProcessingStatus.PENDING)
                .build();

        attachmentRepository.save(attachment);

        // 6. Fire Event for Background Job
        eventPublisher.publishEvent(new ImageUploadedEvent(
                attachment.getId(),
                targetLocation.toString(),
                todoUuid));
    }
}
