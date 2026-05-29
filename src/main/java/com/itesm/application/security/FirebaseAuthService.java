package com.itesm.application.security;

import com.google.firebase.auth.FirebaseAuthException;

public interface FirebaseAuthService {
    String createUser(String email, String password) throws FirebaseAuthException;

    void deleteUser(String uid) throws FirebaseAuthException;
}
