package com.on3ss.todo_app.modules.media.jobs;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.on3ss.todo_app.modules.media.domain.Attachment;
import com.on3ss.todo_app.modules.media.domain.Attachment.ProcessingStatus;
import com.on3ss.todo_app.modules.media.events.ImageUploadedEvent;
import com.on3ss.todo_app.modules.media.repository.AttachmentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ImageProcessorJob {
    private final AttachmentRepository attachmentRepository;
    private final ThumbnailGenerator thumbnailGenerator;

    @Async("imageProcessorExecutor")
    @EventListener
    @Transactional
    public void handleImageProcessing(ImageUploadedEvent event) {
        Attachment attachment = attachmentRepository.findById(event.attachmentId())
                .orElseThrow(() -> new RuntimeException("Attachment record lost!"));

        try {
            String thumbPath = thumbnailGenerator.generate(event.filePath());

            attachment.setThumbnailPath(thumbPath);
            attachment.setStatus(ProcessingStatus.COMPLETED);

            attachmentRepository.save(attachment);

        } catch (Exception e) {
            attachment.setStatus(ProcessingStatus.FAILED);
            attachmentRepository.save(attachment);
        }
    }
}
