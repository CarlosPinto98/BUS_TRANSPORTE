package com.unimag.DTO;

import com.unimag.entities.Enums.Role;
import com.unimag.entities.Enums.StatusUser;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UserDTO {

    public record userCreateRequest(
            @NotBlank(message = "name is required")
            String name,
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
            String password) implements Serializable {}

    public record userUpdateRequest(
            @NotBlank(message = "username is required")
            String name,
            @NotBlank(message = "phone is required")
            @Pattern(regexp = "\\d{10}")
            String phone,
            @NotNull(message = "status is required")
            StatusUser statusUser) implements Serializable {}

//    public record userUpdateRequest(
//            @NotBlank(message = "name is required")
//            String name,
//
//            @NotBlank(message = "phone is required")
//            @Pattern(regexp = "\\d{10}")
//            String phone,
//
//            @Email(message = "email must be valid")
//            String email) implements Serializable {}

    public record UserCheckRequest(
            @Email String email,
            @Pattern(regexp = "\\d{10}") String phone) implements Serializable {}

    public record UserAvailabilityResponse(
            boolean emailAvailable,
            boolean phoneAvailable) implements Serializable {}

    public record userResponse(
            Long id,
            String name,
            String email,
            String phone,
            String role,
            String statusUser,
            LocalDateTime createAt) implements Serializable {}
}
