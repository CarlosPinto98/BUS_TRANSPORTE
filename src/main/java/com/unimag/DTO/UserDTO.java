package com.unimag.DTO;

import com.unimag.entities.Enums.Role;
import com.unimag.entities.Enums.StatusUser;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UserDTO {

    public record UserCreateRequest(
            @NotBlank(message = "username is required")
            String username,
            @NotBlank(message = "email is required")
            @Email(message = "email must be valid")
            String email,
            @NotBlank(message = "phone is required")
            @Pattern(regexp = "\\d{10}",message = "phone length must be 10" )
            String phone,
            @NotNull(message = "role is required")
            Role role,
            @NotBlank(message = "password is required")
            @Size(min = 8, message = "password must be at least 8 characters")
            String password
    ) implements Serializable {}

    public record UserUpdateRequest(
            @NotBlank(message = "username is required")
            String username,
            @NotBlank(message = "phone is required")
            @Pattern(regexp = "\\d{10}")
            String phone,
            @NotNull(message = "status is required")
            StatusUser statusUser
    ) implements Serializable {}

    public record UserResponse(
            Long id,
            String username,
            String email,
            String phone,
            String role,
            String status,
            LocalDateTime createAt
    ) implements Serializable {}
}
