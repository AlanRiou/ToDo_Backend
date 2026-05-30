package com.itesm.infrastructure.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.security.CurrentUser;
import com.itesm.domain.models.User;
import com.itesm.domain.repository.UserRepository;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import io.quarkus.arc.profile.UnlessBuildProfile;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Provider
@Priority(Priorities.AUTHENTICATION)
@UnlessBuildProfile("test")
public class FirebaseAuthFilter implements ContainerRequestFilter {
    @Inject
    UserRepository userRepository;
    @Inject
    AuthenticatedUserContext authenticatedUserContext;


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        if(path.equals("/status") || path.equals("status")){
            return;
        }
        String authHeader = requestContext.getHeaders().getFirst("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            requestContext.abortWith(error(Response.Status.UNAUTHORIZED, "Your session expired. Sign in again."));
            return;
        }
        try {
            assert authHeader != null;
            FirebaseToken decodedToken= FirebaseAuth.getInstance().verifyIdToken(authHeader.replace("Bearer ",""),true);
            Optional<User> userOptional = userRepository.findByFirebaseUuid(decodedToken.getUid());
            if (path.equals("/user") || path.equals("user")) {
                User user = userOptional.orElse(null);
                authenticatedUserContext.setCurrentUser(new CurrentUser(
                        user != null ? user.getId() : null,
                        decodedToken.getUid(),
                        decodedToken.getEmail(),
                        user != null ? user.getRole() : "USER",
                        user != null ? user.getFullName() : decodedToken.getName()
                ));
                return;
            }
            if(userOptional.isEmpty()){
                requestContext.abortWith(error(Response.Status.UNAUTHORIZED, "Your session expired. Sign in again."));
                return;
            }
            User user= userOptional.get();
            CurrentUser currentUser= new CurrentUser(
                    user.getId(),user.getFirebaseUuid(),user.getEmail(),user.getRole(), user.getFullName()
            );
            authenticatedUserContext.setCurrentUser(currentUser);
        } catch (FirebaseAuthException e) {
            requestContext.abortWith(error(Response.Status.UNAUTHORIZED, "Your session expired. Sign in again."));
        }

    }

    private Response error(Response.Status status, String message) {
        return Response.status(status).entity(Map.of("message", message)).build();
    }
}
