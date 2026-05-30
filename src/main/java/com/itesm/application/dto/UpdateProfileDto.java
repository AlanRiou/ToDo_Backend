package com.itesm.application.dto;

import jakarta.validation.constraints.Email;

public class UpdateProfileDto {
    private String fullName;

    @Email(message = "El email debe tener un formato valido")
    private String email;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
