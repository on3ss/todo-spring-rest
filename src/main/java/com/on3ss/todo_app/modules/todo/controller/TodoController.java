package com.on3ss.todo_app.modules.todo.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.on3ss.todo_app.modules.todo.domain.Todo;
import com.on3ss.todo_app.modules.todo.dto.TodoRequest;
import com.on3ss.todo_app.modules.todo.dto.TodoStatusRequest;
import com.on3ss.todo_app.modules.todo.service.TodoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<Todo> createTodo(@Valid @RequestBody TodoRequest request, Principal principal){
        return ResponseEntity.status(HttpStatus.CREATED).body(todoService.createTodo(request, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getMyTodos(Principal principal){
        return ResponseEntity.ok(todoService.getMyTodos(principal.getName()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Todo> toggle(@PathVariable UUID id, @RequestBody TodoStatusRequest request, Principal principal){
        return ResponseEntity.ok(todoService.updateStatus(id, request.isCompleted(), principal.getName()));
    }
    
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Todo> getAllSystemTodos() {
        return todoService.findAll();
    }
}
