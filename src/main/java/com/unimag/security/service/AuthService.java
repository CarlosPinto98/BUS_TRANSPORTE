package com.unimag.security.service;

import com.github.dockerjava.api.model.AuthResponse;
import com.unimag.DTO.AuthDTO;

public interface AuthService {

    AuthResponse register(AuthDTO.RegisterRequest request);
    AuthResponse login(AuthDTO.LoginRequest request);
    AuthResponse loginWithPhone(AuthDTO.PhoneLoginRequest request);
    AuthResponse refreshToken(AuthDTO.RefreshTokenRequest request);
    AuthResponse generateOfflineToken(String userEmail);
    AuthDTO.MessageResponse changePassword(String userEmail, AuthDTO.ChangePasswordRequest request);

    AuthDTO.MessageResponse forgotPassword(AuthDTO.ForgotPasswordRequest request);
    AuthDTO.MessageResponse resetPassword(AuthDTO.ResetPasswordRequest request);

    AuthDTO.MessageResponse logout(AuthDTO.LogoutRequest request);
    AuthDTO.TokenValidationResponse validateToken(String token);
}
