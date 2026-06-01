package com.itesm.application.usecase;

import com.itesm.application.dto.RegisterUserDto;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.security.CurrentUser;
import com.itesm.domain.models.User;
import com.itesm.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisterUserUseCaseTest {

    @Test
    void execute_updatesEmailPrefixNameWhenExplicitNameArrives() {
        UserRepository userRepository = mock(UserRepository.class);
        AuthenticatedUserContext context = authenticatedUser("fb-123", "jane@example.com");
        RegisterUserUseCase useCase = new RegisterUserUseCase(userRepository, context);
        User existingUser = user("jane", "jane@example.com");
        RegisterUserDto request = registerRequest("Jane Doe", "jane@example.com");

        when(userRepository.findByFirebaseUuid("fb-123")).thenReturn(Optional.of(existingUser));
        when(userRepository.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = useCase.execute(request);

        assertEquals("Jane Doe", result.getFullName());
        verify(userRepository).update(existingUser);
    }

    @Test
    void execute_doesNotOverwriteExistingNameWithEmailPrefixFallback() {
        UserRepository userRepository = mock(UserRepository.class);
        AuthenticatedUserContext context = authenticatedUser("fb-123", "jane@example.com");
        RegisterUserUseCase useCase = new RegisterUserUseCase(userRepository, context);
        User existingUser = user("Jane Doe", "jane@example.com");
        RegisterUserDto request = registerRequest("jane", "jane@example.com");

        when(userRepository.findByFirebaseUuid("fb-123")).thenReturn(Optional.of(existingUser));

        User result = useCase.execute(request);

        assertEquals("Jane Doe", result.getFullName());
        verify(userRepository, never()).update(any(User.class));
    }

    private AuthenticatedUserContext authenticatedUser(String firebaseUuid, String email) {
        AuthenticatedUserContext context = new AuthenticatedUserContext();
        context.setCurrentUser(new CurrentUser(null, firebaseUuid, email, "USER", null));
        return context;
    }

    private RegisterUserDto registerRequest(String fullName, String email) {
        RegisterUserDto request = new RegisterUserDto();
        request.setFullName(fullName);
        request.setEmail(email);
        return request;
    }

    private User user(String fullName, String email) {
        User user = new User();
        user.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setActive(true);
        user.setFirebaseUuid("fb-123");
        user.setRole("USER");
        user.setPreferredLanguage("en");
        return user;
    }
}
