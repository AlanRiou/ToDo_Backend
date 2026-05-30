package com.itesm.interfaces.rest;

import com.itesm.application.dto.TaskRequest;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.domain.models.Todo;
import com.itesm.domain.repository.TodoRepository;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
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

@Path("/tasks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {
    @Inject
    TodoRepository todoRepository;

    @Inject
    AuthenticatedUserContext authenticatedUserContext;

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") UUID id) {
        return Response.ok(findOwnedTask(id)).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, @Valid TaskRequest request) {
        Todo todo = findOwnedTask(id);
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        if (request.getCompleted() != null) {
            todo.setCompleted(request.getCompleted());
        }
        todo.setPriority(request.getPriority() == null || request.getPriority().isBlank() ? todo.getPriority() : request.getPriority());
        todo.setDueDate(request.getDueDate());
        todo.setUpdatedAt(LocalDateTime.now());
        return Response.ok(todoRepository.update(todo)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        todoRepository.delete(findOwnedTask(id));
        return Response.ok(Map.of("deleted", true)).build();
    }

    private Todo findOwnedTask(UUID id) {
        return todoRepository.findByIdAndUserId(id, authenticatedUserContext.getCurrentUser().getUserId())
                .orElseThrow(NotFoundException::new);
    }
}
