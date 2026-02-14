package com.on3ss.todo_app.modules.todo.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoResponse {
    private UUID uuid;
    private String title;
    private String description;
    private boolean completed;
    private List<AttachmentResponse> attachments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttachmentResponse {
        private UUID id;
        private String fileName;
        private String url;
        private String thumbnailUrl;
    }
}
