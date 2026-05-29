package com.itesm.domain.repository;

import com.itesm.domain.models.TaskList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskListRepository {
    TaskList save(TaskList taskList);
    List<TaskList> findByUserId(UUID userId);
    Optional<TaskList> findByIdAndUserId(UUID id, UUID userId);
    List<TaskList> search(UUID userId, String query);
    TaskList update(TaskList taskList);
    void delete(TaskList taskList);
}
