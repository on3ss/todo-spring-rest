package com.on3ss.todo_app.modules.todo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.on3ss.todo_app.modules.todo.domain.Todo;

public interface TodoRepository extends JpaRepository<Todo, UUID> {
    List<Todo> findByOwnerEmail(String email);
}
