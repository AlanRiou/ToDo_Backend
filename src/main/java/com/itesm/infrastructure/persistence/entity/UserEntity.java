package com.itesm.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="users")
public class UserEntity {
    @Id
    private UUID id;
    @Column(nullable = false, length = 255, name="full_name")
    private String fullName;
    @Column(nullable = false, length = 255, unique = true )
    private String email;
    @Column(nullable = false)
    private boolean active;
    @Column(name="firebase_uuid", unique = true, length = 128)
    private String firebaseUuid;
    @Column(nullable = false, name="created_at")
    private LocalDateTime createdAt;
    @Column(nullable = false, name="updated_at")
    private LocalDateTime updatedAt;
    @Column
    private String role;
    public UserEntity(){

    }
    public UserEntity(UUID id, String fullName, String email, boolean active, String firebaseUuid, LocalDateTime createdAt, LocalDateTime updatedAt, String role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.active = active;
        this.firebaseUuid = firebaseUuid;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFirebaseUuid() {
        return firebaseUuid;
    }

    public void setFirebaseUuid(String firebaseUuid) {
        this.firebaseUuid = firebaseUuid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
