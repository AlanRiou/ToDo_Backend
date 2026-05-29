package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.TaskList;
import com.itesm.infrastructure.persistence.entity.TaskListEntity;

public class TaskListMapper {
    public static TaskListEntity toEntity(TaskList taskList) {
        TaskListEntity entity = new TaskListEntity();
        entity.setId(taskList.getId());
        entity.setUserId(taskList.getUserId());
        entity.setTitle(taskList.getTitle());
        entity.setDescription(taskList.getDescription());
        entity.setAccentColor(taskList.getAccentColor());
        entity.setIcon(taskList.getIcon());
        entity.setCreatedAt(taskList.getCreatedAt());
        entity.setUpdatedAt(taskList.getUpdatedAt());
        return entity;
    }

    public static TaskList toDomain(TaskListEntity entity) {
        TaskList taskList = new TaskList();
        taskList.setId(entity.getId());
        taskList.setUserId(entity.getUserId());
        taskList.setTitle(entity.getTitle());
        taskList.setDescription(entity.getDescription());
        taskList.setAccentColor(entity.getAccentColor());
        taskList.setIcon(entity.getIcon());
        taskList.setCreatedAt(entity.getCreatedAt());
        taskList.setUpdatedAt(entity.getUpdatedAt());
        return taskList;
    }
}
