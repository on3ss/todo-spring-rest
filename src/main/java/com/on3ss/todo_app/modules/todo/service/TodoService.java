package com.on3ss.todo_app.modules.todo.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.on3ss.todo_app.infrastructure.exceptions.BusinessException;
import com.on3ss.todo_app.modules.auth.domain.User;
import com.on3ss.todo_app.modules.auth.repository.UserRepository;
import com.on3ss.todo_app.modules.media.domain.Attachment;
import com.on3ss.todo_app.modules.todo.domain.Todo;
import com.on3ss.todo_app.modules.todo.dto.TodoRequest;
import com.on3ss.todo_app.modules.todo.dto.TodoResponse;
import com.on3ss.todo_app.modules.todo.repository.TodoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Caching(evict = {
            @CacheEvict(value = "todos", key = "#userEmail"),
            @CacheEvict(value = "allTodos", allEntries = true)
    })
    @Transactional
    public Todo createTodo(TodoRequest request, String userEmail) {
        User owner = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Todo todo = Todo.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .owner(owner)
                .build();

        return todoRepository.save(todo);
    }

    @Cacheable(value = "todos", key = "#userEmail")
    public List<TodoResponse> getMyTodos(String userEmail) {
        List<Todo> todos = todoRepository.findByOwnerEmail(userEmail);
        List<TodoResponse> response = todos.stream()
                .map(this::getTodoAsDto)
                .collect(Collectors.toList());

        return response;
    }

    @Caching(evict = {
        @CacheEvict(value = "todos", key = "#userEmail"),
        @CacheEvict(value = "allTodos", allEntries = true)
    })
    @Transactional
    public Todo updateStatus(UUID id, boolean completed, String userEmail) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Todo not found"));

        if (!todo.getOwner().getEmail().equals(userEmail)) {
            throw new BusinessException("You do not have permission to update this todo");
        }

        todo.setCompleted(completed);
        return todoRepository.save(todo);
    }

    @Cacheable(value = "allTodos")
    public List<Todo> findAll() {
        return todoRepository.findAll();
    }

    public TodoResponse getTodoAsDto(Todo todo) {
        return TodoResponse.builder()
                .uuid(todo.getUuid())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .completed(todo.isCompleted())
                .attachments(todo.getAttachments().stream()
                        .map(this::mapToAttachmentResponse)
                        .toList())
                .build();
    }

    private TodoResponse.AttachmentResponse mapToAttachmentResponse(Attachment attachment) {
        return TodoResponse.AttachmentResponse.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                // Formatting the paths to URLs here
                .url(formatToUrl(attachment.getFilePath()))
                .thumbnailUrl(formatToUrl(attachment.getThumbnailPath()))
                .build();
    }

    private String formatToUrl(String path) {
        if (path == null)
            return null;
        String fileName = new java.io.File(path).getName();

        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/")
                .path(fileName)
                .toUriString();
    }
}
