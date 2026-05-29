package com.itesm.interfaces.rest;

import com.itesm.application.security.AuthenticatedUserContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/me")
@Produces(MediaType.APPLICATION_JSON)
public class MeResource {
    @Inject
    AuthenticatedUserContext authenticatedUserContext;

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
}
