package com.itesm.domain.repository;

import com.itesm.domain.models.Todo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface TodoRepository {
    Todo save(Todo todo);
    List<Todo> findAllTodos();
    List<Todo> findByTaskListIdAndUserId(UUID taskListId, UUID userId);
    Optional<Todo> findByIdAndUserId(UUID id, UUID userId);
    List<Todo> search(UUID userId, String query);
    long countByTaskListId(UUID taskListId);
    long countCompletedByTaskListId(UUID taskListId);
    Todo update(Todo todo);
    void delete(Todo todo);
    void deleteByTaskListId(UUID taskListId);
}
