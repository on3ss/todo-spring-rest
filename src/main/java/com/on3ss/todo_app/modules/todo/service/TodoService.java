package com.on3ss.todo_app.modules.todo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.on3ss.todo_app.infrastructure.exceptions.BusinessException;
import com.on3ss.todo_app.modules.auth.domain.User;
import com.on3ss.todo_app.modules.auth.repository.UserRepository;
import com.on3ss.todo_app.modules.todo.domain.Todo;
import com.on3ss.todo_app.modules.todo.dto.TodoRequest;
import com.on3ss.todo_app.modules.todo.repository.TodoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

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

    public List<Todo> getMyTodos(String userEmail) {
        return todoRepository.findByOwnerEmail(userEmail);
    }

    public Todo updateStatus(UUID id, boolean completed, String userEmail) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Todo not found"));

        if (!todo.getOwner().getEmail().equals(userEmail)) {
            throw new BusinessException("You do not have permission to update this todo");
        }

        todo.setCompleted(completed);
        return todoRepository.save(todo);
    }

    public List<Todo> findAll() {
        return todoRepository.findAll();
    }
}
