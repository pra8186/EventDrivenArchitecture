package com.eventdriven.core.dto.response;

import com.eventdriven.core.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for user data. Excludes password hash and SSN.
 */
public class UserResponse {

    private final UUID userId;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final LocalDateTime createdAt;

    private UserResponse(UUID userId, String email, String firstName,
                         String lastName, LocalDateTime createdAt) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
    }

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getUserId(), user.getEmail(),
                user.getFirstName(), user.getLastName(),
                user.getCreatedAt()
        );
    }

    public UUID getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
