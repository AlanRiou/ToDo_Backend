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
        return userRepository.findByFirebaseUuid(currentUser.getFirebaseUuid()).orElseGet(() -> createUser(registerUserDto));
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
}
