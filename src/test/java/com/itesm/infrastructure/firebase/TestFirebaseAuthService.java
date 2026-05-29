package com.itesm.infrastructure.firebase;

import com.google.firebase.auth.FirebaseAuthException;
import com.itesm.application.security.FirebaseAuthService;
import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@IfBuildProfile("test")
public class TestFirebaseAuthService implements FirebaseAuthService {
    @Override
    public String createUser(String email, String password) throws FirebaseAuthException {
        return "test-firebase-uid";
    }

    @Override
    public void deleteUser(String uid) throws FirebaseAuthException {
        // No-op for tests.
    }
}
