package com.on3ss.todo_app.modules.media.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.on3ss.todo_app.modules.media.domain.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    List<Attachment> findByTodoUuid(UUID todoUuid);
}
