package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.TaskList;
import com.itesm.domain.repository.TaskListRepository;
import com.itesm.infrastructure.mapper.TaskListMapper;
import com.itesm.infrastructure.persistence.entity.TaskListEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TaskListRepositoryImpl implements TaskListRepository, PanacheRepositoryBase<TaskListEntity, UUID> {
    @Override
    @Transactional
    public TaskList save(TaskList taskList) {
        TaskListEntity entity = TaskListMapper.toEntity(taskList);
        persist(entity);
        return TaskListMapper.toDomain(entity);
    }

    @Override
    public List<TaskList> findByUserId(UUID userId) {
        return find("userId = ?1 order by updatedAt desc", userId)
                .stream()
                .map(TaskListMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<TaskList> findByIdAndUserId(UUID id, UUID userId) {
        return find("id = ?1 and userId = ?2", id, userId)
                .firstResultOptional()
                .map(TaskListMapper::toDomain);
    }

    @Override
    public List<TaskList> search(UUID userId, String query) {
        String likeQuery = "%" + query.toLowerCase() + "%";
        return find("userId = ?1 and (lower(title) like ?2 or lower(description) like ?2) order by updatedAt desc", userId, likeQuery)
                .stream()
                .map(TaskListMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public TaskList update(TaskList taskList) {
        TaskListEntity entity = findById(taskList.getId());
        entity.setTitle(taskList.getTitle());
        entity.setDescription(taskList.getDescription());
        entity.setAccentColor(taskList.getAccentColor());
        entity.setIcon(taskList.getIcon());
        entity.setUpdatedAt(taskList.getUpdatedAt());
        return TaskListMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public void delete(TaskList taskList) {
        deleteById(taskList.getId());
    }
}
