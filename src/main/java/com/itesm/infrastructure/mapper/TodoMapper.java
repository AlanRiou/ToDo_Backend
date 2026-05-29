package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.Todo;
import com.itesm.infrastructure.persistence.entity.TodoEntity;

public class TodoMapper {

    public static TodoEntity toEntity(Todo todo) {
        TodoEntity todoEntity = new TodoEntity();
        todoEntity.setId(todo.getUuid());
        todoEntity.setUserId(todo.getUserId());
        todoEntity.setTaskListId(todo.getTaskListId());
        todoEntity.setTitle(todo.getTitle());
        todoEntity.setDescription(todo.getDescription());
        todoEntity.setCompleted(todo.isCompleted());
        todoEntity.setPriority(todo.getPriority());
        todoEntity.setDueDate(todo.getDueDate());
        todoEntity.setCreatedAt(todo.getCreatedAt());
        todoEntity.setUpdatedAt(todo.getUpdatedAt());
        return todoEntity;
    }

    public static Todo toDomain(TodoEntity todoEntity) {
        Todo todo = new Todo();
        todo.setUuid(todoEntity.getId());
        todo.setUserId(todoEntity.getUserId());
        todo.setTaskListId(todoEntity.getTaskListId());
        todo.setTitle(todoEntity.getTitle());
        todo.setDescription(todoEntity.getDescription());
        todo.setCompleted(todoEntity.isCompleted());
        todo.setPriority(todoEntity.getPriority());
        todo.setDueDate(todoEntity.getDueDate());
        todo.setCreatedAt(todoEntity.getCreatedAt());
        todo.setUpdatedAt(todoEntity.getUpdatedAt());
        return todo;
    }
}
