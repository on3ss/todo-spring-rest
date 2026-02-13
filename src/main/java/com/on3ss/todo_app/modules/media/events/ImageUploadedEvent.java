package com.on3ss.todo_app.modules.media.events;

import java.util.UUID;

public record ImageUploadedEvent(
    UUID attachmentId,
    String filePath,
    UUID todoUUid
) {}
