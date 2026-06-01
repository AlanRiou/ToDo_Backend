package com.itesm.application.usecase;

import com.itesm.application.dto.RegisterUserDto;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.domain.models.User;
import com.itesm.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class RegisterUserUseCase {

    @Inject
    private UserRepository userRepository;

    @Inject
    private AuthenticatedUserContext authenticatedUserContext;

    public RegisterUserUseCase(UserRepository userRepository, AuthenticatedUserContext authenticatedUserContext) {
        this.userRepository = userRepository;
        this.authenticatedUserContext = authenticatedUserContext;
    }

    public User execute(RegisterUserDto registerUserDto) {
        var currentUser = authenticatedUserContext.getCurrentUser();
        return userRepository.findByFirebaseUuid(currentUser.getFirebaseUuid())
                .map(existingUser -> syncExistingUser(existingUser, registerUserDto))
                .orElseGet(() -> createUser(registerUserDto));
    }

    private User createUser(RegisterUserDto registerUserDto) {
        var currentUser = authenticatedUserContext.getCurrentUser();
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setActive(true);
        user.setFullName(registerUserDto.getFullName());
        user.setEmail(registerUserDto.getEmail());
        user.setFirebaseUuid(currentUser.getFirebaseUuid());
        user.setRole("USER");
        user.setPreferredLanguage("en");
        return userRepository.create(user);
    }

    private User syncExistingUser(User user, RegisterUserDto registerUserDto) {
        var requestedName = normalize(registerUserDto.getFullName());
        var requestedEmail = normalize(registerUserDto.getEmail());
        var changed = false;

        if (!requestedName.isBlank() && shouldUpdateName(user.getFullName(), requestedName, requestedEmail)) {
            user.setFullName(requestedName);
            changed = true;
        }
        if (!requestedEmail.isBlank() && !requestedEmail.equals(user.getEmail())) {
            user.setEmail(requestedEmail);
            changed = true;
        }

        return changed ? userRepository.update(user) : user;
    }

    private boolean shouldUpdateName(String currentName, String requestedName, String requestedEmail) {
        if (requestedName.equals(currentName)) {
            return false;
        }
        return isBlank(currentName) || isEmailPrefix(currentName, requestedEmail) || !isEmailPrefix(requestedName, requestedEmail);
    }

    private boolean isEmailPrefix(String value, String email) {
        if (isBlank(value) || isBlank(email) || !email.contains("@")) {
            return false;
        }
        return value.equalsIgnoreCase(email.substring(0, email.indexOf("@")));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isBlank();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
