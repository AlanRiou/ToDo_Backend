package com.itesm.interfaces.rest;

import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.domain.repository.TaskListRepository;
import com.itesm.domain.repository.TodoRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;
import java.util.UUID;

@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {
    @Inject
    TaskListRepository taskListRepository;

    @Inject
    TodoRepository todoRepository;

    @Inject
    AuthenticatedUserContext authenticatedUserContext;

    @GET
    public Response search(@QueryParam("q") String query) {
        String normalizedQuery = query == null ? "" : query.trim();
        UUID userId = authenticatedUserContext.getCurrentUser().getUserId();
        if (normalizedQuery.isBlank()) {
            return Response.ok(Map.of("taskLists", taskListRepository.findByUserId(userId), "tasks", todoRepository.search(userId, ""))).build();
        }
        return Response.ok(Map.of(
                "taskLists", taskListRepository.search(userId, normalizedQuery),
                "tasks", todoRepository.search(userId, normalizedQuery)
        )).build();
    }
}
