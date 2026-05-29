package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.Todo;
import com.itesm.domain.repository.TodoRepository;
import com.itesm.infrastructure.mapper.TodoMapper;
import com.itesm.infrastructure.persistence.entity.TodoEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TodoRepositoryImpl implements TodoRepository, PanacheRepositoryBase<TodoEntity, UUID> {
    @Override
    @Transactional
    public Todo save(Todo todo) {
        System.out.println("Entra a repository");
        TodoEntity entity= TodoMapper.toEntity(todo);
        persist(entity);
        return TodoMapper.toDomain(entity);
    }

    @Override
    public List<Todo> findAllTodos() {
        List<TodoEntity> todoEntities= findAll().stream().toList();
        List<Todo> todos = new ArrayList<>();
        for(TodoEntity todoEntity : todoEntities){
            todos.add(TodoMapper.toDomain(todoEntity));
        }
        return todos;
    }

    @Override
    public List<Todo> findByTaskListIdAndUserId(UUID taskListId, UUID userId) {
        return find("taskListId = ?1 and userId = ?2 order by completed asc, createdAt desc", taskListId, userId)
                .stream()
                .map(TodoMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Todo> findByIdAndUserId(UUID id, UUID userId) {
        return find("id = ?1 and userId = ?2", id, userId)
                .firstResultOptional()
                .map(TodoMapper::toDomain);
    }

    @Override
    public List<Todo> search(UUID userId, String query) {
        String likeQuery = "%" + query.toLowerCase() + "%";
        return find("userId = ?1 and (lower(title) like ?2 or lower(description) like ?2) order by updatedAt desc", userId, likeQuery)
                .stream()
                .map(TodoMapper::toDomain)
                .toList();
    }

    @Override
    public long countByTaskListId(UUID taskListId) {
        return count("taskListId", taskListId);
    }

    @Override
    public long countCompletedByTaskListId(UUID taskListId) {
        return count("taskListId = ?1 and completed = true", taskListId);
    }

    @Override
    @Transactional
    public Todo update(Todo todo) {
        TodoEntity entity = findById(todo.getUuid());
        entity.setTitle(todo.getTitle());
        entity.setDescription(todo.getDescription());
        entity.setCompleted(todo.isCompleted());
        entity.setPriority(todo.getPriority());
        entity.setDueDate(todo.getDueDate());
        entity.setUpdatedAt(todo.getUpdatedAt());
        return TodoMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public void delete(Todo todo) {
        deleteById(todo.getUuid());
    }

    @Override
    @Transactional
    public void deleteByTaskListId(UUID taskListId) {
        delete("taskListId", taskListId);
    }


}
