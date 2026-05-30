package com.itesm.interfaces.rest;

import com.itesm.application.dto.TaskListRequest;
import com.itesm.application.dto.TaskRequest;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.domain.models.TaskList;
import com.itesm.domain.models.Todo;
import com.itesm.domain.repository.TaskListRepository;
import com.itesm.domain.repository.TodoRepository;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Path("/task-lists")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TaskListResource {
    @Inject
    TaskListRepository taskListRepository;

    @Inject
    TodoRepository todoRepository;

    @Inject
    AuthenticatedUserContext authenticatedUserContext;

    @GET
    public Response list() {
        return Response.ok(taskListRepository.findByUserId(userId()).stream().map(this::withCounts).toList()).build();
    }

    @POST
    public Response create(@Valid TaskListRequest request) {
        LocalDateTime now = LocalDateTime.now();
        TaskList taskList = new TaskList();
        taskList.setId(UUID.randomUUID());
        taskList.setUserId(userId());
        taskList.setTitle(request.getTitle());
        taskList.setDescription(request.getDescription());
        taskList.setAccentColor(defaultString(request.getAccentColor(), "#0B72E7"));
        taskList.setIcon(defaultString(request.getIcon(), "book"));
        taskList.setCreatedAt(now);
        taskList.setUpdatedAt(now);
        return Response.status(Response.Status.CREATED).entity(withCounts(taskListRepository.save(taskList))).build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") UUID id) {
        return Response.ok(withCounts(findOwnedList(id))).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, @Valid TaskListRequest request) {
        TaskList taskList = findOwnedList(id);
        taskList.setTitle(request.getTitle());
        taskList.setDescription(request.getDescription());
        taskList.setAccentColor(defaultString(request.getAccentColor(), taskList.getAccentColor()));
        taskList.setIcon(defaultString(request.getIcon(), taskList.getIcon()));
        taskList.setUpdatedAt(LocalDateTime.now());
        return Response.ok(withCounts(taskListRepository.update(taskList))).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        TaskList taskList = findOwnedList(id);
        todoRepository.deleteByTaskListId(id);
        taskListRepository.delete(taskList);
        return Response.ok(Map.of("deleted", true)).build();
    }

    @GET
    @Path("/{id}/tasks")
    public Response listTasks(@PathParam("id") UUID id) {
        findOwnedList(id);
        return Response.ok(todoRepository.findByTaskListIdAndUserId(id, userId())).build();
    }

    @POST
    @Path("/{id}/tasks")
    public Response createTask(@PathParam("id") UUID id, @Valid TaskRequest request) {
        findOwnedList(id);
        LocalDateTime now = LocalDateTime.now();
        Todo todo = new Todo();
        todo.setUuid(UUID.randomUUID());
        todo.setUserId(userId());
        todo.setTaskListId(id);
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(Boolean.TRUE.equals(request.getCompleted()));
        todo.setPriority(defaultString(request.getPriority(), "medium"));
        todo.setDueDate(request.getDueDate());
        todo.setCreatedAt(now);
        todo.setUpdatedAt(now);
        return Response.status(Response.Status.CREATED).entity(todoRepository.save(todo)).build();
    }

    private TaskList findOwnedList(UUID id) {
        return taskListRepository.findByIdAndUserId(id, userId()).orElseThrow(NotFoundException::new);
    }

    private TaskList withCounts(TaskList taskList) {
        taskList.setTotalTasks(todoRepository.countByTaskListId(taskList.getId()));
        taskList.setCompletedTasks(todoRepository.countCompletedByTaskListId(taskList.getId()));
        return taskList;
    }

    private UUID userId() {
        return authenticatedUserContext.getCurrentUser().getUserId();
    }

    private String defaultString(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
