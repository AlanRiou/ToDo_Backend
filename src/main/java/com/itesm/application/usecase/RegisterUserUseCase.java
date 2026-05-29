package com.itesm.application.usecase;

import com.google.firebase.auth.FirebaseAuthException;
import com.itesm.application.dto.RegisterUserDto;
import com.itesm.application.security.FirebaseAuthService;
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
    private FirebaseAuthService firebaseAuthService;

    public RegisterUserUseCase(UserRepository userRepository, FirebaseAuthService firebaseAuthService) {
        this.userRepository = userRepository;
        this.firebaseAuthService = firebaseAuthService;
    }

    public User execute(RegisterUserDto registerUserDto) throws FirebaseAuthException {
        String firebaseUuid = firebaseAuthService.createUser(
                registerUserDto.getEmail(),
                registerUserDto.getPassword()
        );

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setActive(true);
        user.setFullName(registerUserDto.getFullName());
        user.setEmail(registerUserDto.getEmail());
        user.setFirebaseUuid(firebaseUuid);
        user.setRole("USER");

        try {
            return userRepository.create(user);
        } catch (RuntimeException e) {
            firebaseAuthService.deleteUser(firebaseUuid);
            throw e;
        }

    }
}
