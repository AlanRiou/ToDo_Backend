package com.itesm.interfaces.rest;

import com.itesm.application.dto.UpdateProfileDto;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.domain.models.User;
import com.itesm.domain.repository.UserRepository;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/me")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MeResource {
    @Inject
    AuthenticatedUserContext authenticatedUserContext;

    @Inject
    UserRepository userRepository;

    @GET
    public Response me() {
        var user = authenticatedUserContext.getCurrentUser();
        return Response.ok(Map.of(
                "id", user.getUserId(),
                "firebaseUuid", user.getFirebaseUuid(),
                "email", user.getEmail(),
                "fullName", user.getFullName(),
                "role", user.getRole()
        )).build();
    }

    @PUT
    public Response update(@Valid UpdateProfileDto request) {
        var currentUser = authenticatedUserContext.getCurrentUser();
        User user = userRepository.findUserById(currentUser.getUserId()).orElseThrow(NotFoundException::new);
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail().trim());
        }
        User updated = userRepository.update(user);
        return Response.ok(Map.of(
                "id", updated.getId(),
                "firebaseUuid", updated.getFirebaseUuid(),
                "email", updated.getEmail(),
                "fullName", updated.getFullName(),
                "role", updated.getRole()
        )).build();
    }
}
