package com.unimag.DTO;

import com.unimag.entities.Enums.Role;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;

public class AuthDTO {


    public record RegisterRequest(
            @NotBlank(message = "Username is required")
            @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
            String username,

            @NotBlank(message = "Email is required")
            @Email(message = "Email must be valid")
            String email,

            @NotBlank(message = "Phone is required")
            @Pattern(regexp = "\\d{10}", message = "Phone must be exactly 10 digits")
            String phone,

            @NotBlank(message = "Password is required")
            @Size(min = 8, message = "Password must be at least 8 characters")
            @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
                    message = "Password must contain at least one letter and one number")
            String password,

            Role role,
            @NotNull(message = "Date of birth is required")
            @Past(message = "Date of birth must be in the past")
            LocalDate dateOfBirth
    ) implements Serializable {}

    public record LoginRequest(
            @NotBlank(message = "Email is required")
            @Email(message = "Email must be valid")
            String email,

            @NotBlank(message = "Password is required")
            String password
    ) implements Serializable {}


    public record PhoneLoginRequest(
            @NotBlank(message = "Phone is required")
            @Pattern(regexp = "\\d{10}", message = "Phone must be exactly 10 digits")
            String phone,

            @NotBlank(message = "Password is required")
            String password
    ) implements Serializable {}

    public record UserInfo(
            Long id,
            String username,
            String email,
            String phone,
            String role,
            String status
    ) implements Serializable {}

    public record AuthResponse(
            String accessToken,
            String refreshToken,
            String tokenType,
            Long expiresIn,
            UserInfo user
    ) implements Serializable {
        public AuthResponse(String accessToken, String refreshToken, Long expiresIn, UserInfo user) {
            this(accessToken, refreshToken, "Bearer", expiresIn, user);
        }

        public static AuthResponse forOffline(String offlineToken, Long expiresIn, UserInfo user) {
            return new AuthResponse(offlineToken, null, "Bearer", expiresIn, user);
        }
    }

    public record RefreshTokenRequest(
            @NotBlank(message = "Refresh token is required")
            String refreshToken
    ) implements Serializable {}


    public record ChangePasswordRequest(
            @NotBlank(message = "Current password is required")
            String currentPassword,

            @NotBlank(message = "New password is required")
            @Size(min = 8, message = "New password must be at least 8 characters")
            @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
                    message = "Password must contain at least one letter and one number")
            String newPassword,

            @NotBlank(message = "Confirmation password is required")
            String confirmPassword
    ) implements Serializable {}

    public record ForgotPasswordRequest(
            @NotBlank(message = "Email is required")
            @Email(message = "Email must be valid")
            String email
    ) implements Serializable {}

    public record ResetPasswordRequest(
            @NotBlank(message = "Reset token is required")
            String token,

            @NotBlank(message = "New password is required")
            @Size(min = 8, message = "Password must be at least 8 characters")
            @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
                    message = "Password must contain at least one letter and one number")
            String newPassword,

            @NotBlank(message = "Confirmation password is required")
            String confirmPassword
    ) implements Serializable {}

    public record MessageResponse(
            String message,
            boolean success
    ) implements Serializable {
        public MessageResponse(String message) {
            this(message, true);
        }
    }

    public record LogoutRequest(
            @NotBlank(message = "Access token is required")
            String accessToken,

            String refreshToken
    ) implements Serializable {}

    public record TokenValidationResponse(
            boolean valid,
            String username,
            String role,
            Long userId,
            Long expiresIn
    ) implements Serializable {}
}
