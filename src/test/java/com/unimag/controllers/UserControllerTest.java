package com.unimag.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unimag.DTO.AuthDTO;
import com.unimag.DTO.UserDTO;
import com.unimag.entities.Enums.Role;
import com.unimag.entities.Enums.StatusUser;
import com.unimag.security.service.AuthService;
import com.unimag.security.service.CustomUserDetails;
import com.unimag.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = {SpringDataWebAutoConfiguration.class}
)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @MockitoBean
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private Authentication authentication;

    @MockitoBean
    private CustomUserDetails userDetails;

    @Autowired
    private UserController userController;

    private UserDTO.userResponse userResponse;
    private UserDTO.userCreateRequest createRequest;
    private UserDTO.userUpdateRequest updateRequest;
    private AuthDTO.ChangePasswordRequest passwordRequest;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testDateTime = LocalDateTime.of(2024, 1, 15, 10, 30);

        userResponse = new UserDTO.userResponse(
                1L,
                "Juan Pérez",
                "juan.perez@example.com",
                "3001234567",
                "DRIVER",
                "ACTIVE",
                testDateTime
        );


        createRequest = new UserDTO.userCreateRequest(
                "Juan Pérez",
                "juan.perez@example.com",
                "3001234567",
                Role.DRIVER,
                "SecurePass123!"
        );


        updateRequest = new UserDTO.userUpdateRequest(
                "Juan Pérez Updated",
                "3009876543",
                StatusUser.ACTIVE
        );

        passwordRequest = new AuthDTO.ChangePasswordRequest(
                "OldPassword123!",
                "NewPassword123!",
                "NewPassword123!"
        );

        when(userDetails.getId()).thenReturn(1L);
        when(userDetails.getUsername()).thenReturn("juan.perez@example.com");
    }

    @Test
    @DisplayName("GET CURRENT USER - Should return current user profile")
    void getCurrentUser() throws Exception {

        Long userId = 1L;
        when(userService.getById(userId)).thenReturn(userResponse);

        UserDTO.userResponse result = userService.getById(userId);

        assertEquals(userResponse.id(), result.id());
        assertEquals("Juan Pérez", result.name());
        verify(userService, times(1)).getById(userId);
    }

    @Test
    @DisplayName("UPDATE CURRENT USER - Should update current user profile")
    void updateCurrentUser() throws Exception {

        Long userId = 1L;
        UserDTO.userResponse updatedResponse = new UserDTO.userResponse(
                userId,
                "Juan Pérez Updated",
                "juan.perez@example.com",
                "3009876543",
                "DRIVER",
                "ACTIVE",
                testDateTime
        );

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userService.update(any(UserDTO.userUpdateRequest.class), eq(userId)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/users/update-me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print());

        UserDTO.userResponse result = userService.update(updateRequest, userId);
        assertEquals("Juan Pérez Updated", result.name());
        assertEquals("3009876543", result.phone());
    }

    @Test
    @DisplayName("CHANGE OWN PASSWORD - Should change password successfully")
    void changeOwnPassword() throws Exception {

        AuthDTO.MessageResponse successResponse = new AuthDTO.MessageResponse(
                "Password changed successfully"
        );

        when(authService.changePassword(anyString(), any(AuthDTO.ChangePasswordRequest.class)))
                .thenReturn(successResponse);

        mockMvc.perform(patch("/api/v1/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRequest)))
                .andDo(print());

        AuthDTO.MessageResponse result = authService.changePassword(
                "user@example.com",
                passwordRequest
        );
        assertEquals("Password changed successfully", result.message());
    }

    @Test
    @DisplayName("CREATE USER - Should create user successfully")
    void create() throws Exception {

        when(userService.create(any(UserDTO.userCreateRequest.class)))
                .thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"))
                .andExpect(jsonPath("$.phone").value("3001234567"))
                .andExpect(jsonPath("$.role").value("DRIVER"))
                .andExpect(jsonPath("$.statusUser").value("ACTIVE"));

        verify(userService, times(1)).create(any(UserDTO.userCreateRequest.class));
    }

    @Test
    @DisplayName("CREATE USER - Should return 400 when email is invalid")
    void create_InvalidEmail() throws Exception {

        UserDTO.userCreateRequest invalidRequest = new UserDTO.userCreateRequest(
                "Juan Pérez",
                "invalid-email", // email inválido
                "3001234567",
                Role.DRIVER,
                "SecurePass123!"
        );

        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any());
    }

    @Test
    @DisplayName("CREATE USER - Should return 400 when phone is invalid")
    void create_InvalidPhone() throws Exception {

        UserDTO.userCreateRequest invalidRequest = new UserDTO.userCreateRequest(
                "Juan Pérez",
                "juan@example.com",
                "123", // teléfono inválido (no tiene 10 dígitos)
                Role.DRIVER,
                "SecurePass123!"
        );

        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any());
    }

    @Test
    @DisplayName("CREATE USER - Should return 400 when password is too short")
    void create_ShortPassword() throws Exception {

        UserDTO.userCreateRequest invalidRequest = new UserDTO.userCreateRequest(
                "Juan Pérez",
                "juan@example.com",
                "3001234567",
                Role.DRIVER,
                "short" // contraseña menor a 8 caracteres
        );

        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any());
    }

    @Test
    @DisplayName("GET USER BY ID - Should retrieve user by ID")
    void getUserById() throws Exception {

        Long userId = 1L;
        when(userService.getById(userId)).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"));

        verify(userService, times(1)).getById(userId);
    }

    @Test
    @DisplayName("GET USER BY ID - Should return 500 when user not found")
    void getUserById_NotFound() throws Exception {

        Long userId = 999L;
        when(userService.getById(userId))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is5xxServerError());

        verify(userService, times(1)).getById(userId);
    }

    @Test
    @DisplayName("GET USER BY EMAIL - Should return true when email exists")
    void getUserByEmail() throws Exception {

        String email = "juan.perez@example.com";
        when(userService.getByEmail(email)).thenReturn(null);

        mockMvc.perform(get("/api/v1/users/email/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).getByEmail(email);
    }

    @Test
    @DisplayName("GET USER BY EMAIL - Should return false when email doesn't exist")
    void getUserByEmail_NotFound() throws Exception {

        String email = "nonexistent@example.com";
        when(userService.getByEmail(email)).thenReturn(null);

        mockMvc.perform(get("/api/v1/users/email/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        verify(userService, times(1)).getByEmail(email);
    }

    @Test
    @DisplayName("GET USER BY PHONE - Should return true when phone exists")
    void getUserByPhone() throws Exception {

        String phone = "3001234567";
        when(userService.getByPhone(phone)).thenReturn(null);

        mockMvc.perform(get("/api/v1/users/phone/{phone}", phone)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).getByPhone(phone);
    }

    @Test
    @DisplayName("GET USER BY PHONE - Should return false when phone doesn't exist")
    void getUserByPhone_NotFound() throws Exception {

        String phone = "3009999999";
        when(userService.getByPhone(phone)).thenReturn(null);

        mockMvc.perform(get("/api/v1/users/phone/{phone}", phone)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        verify(userService, times(1)).getByPhone(phone);
    }

    @Test
    @DisplayName("GET ALL USERS - Should retrieve all users")
    void getAllUsers() throws Exception {

        UserDTO.userResponse user1 = new UserDTO.userResponse(
                1L, "User 1", "user1@example.com", "3001111111", "DRIVER", "ACTIVE", testDateTime
        );
        UserDTO.userResponse user2 = new UserDTO.userResponse(
                2L, "User 2", "user2@example.com", "3002222222", "ADMIN", "ACTIVE", testDateTime
        );
        UserDTO.userResponse user3 = new UserDTO.userResponse(
                3L, "User 3", "user3@example.com", "3003333333", "CLERK", "INACTIVE", testDateTime
        );

        List<UserDTO.userResponse> users = Arrays.asList(user1, user2, user3);
        Page<UserDTO.userResponse> page = new PageImpl<>(users, PageRequest.of(0, 10), users.size());

        when(userService.getAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/users/all-users")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[0].role").value("DRIVER"))
                .andExpect(jsonPath("$[1].name").value("User 2"))
                .andExpect(jsonPath("$[1].role").value("ADMIN"))
                .andExpect(jsonPath("$[2].statusUser").value("INACTIVE"));

        verify(userService, times(1)).getAll(any(Pageable.class));
    }

    @Test
    @DisplayName("GET ALL USERS - Should return empty list")
    void getAllUsers_EmptyList() throws Exception {

        Page<UserDTO.userResponse> emptyPage = new PageImpl<>(
                List.of(), PageRequest.of(0, 10), 0
        );

        when(userService.getAll(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/v1/users/all-users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService, times(1)).getAll(any(Pageable.class));
    }

    @Test
    @DisplayName("DELETE USER - Should delete user successfully")
    void deleteUser() throws Exception {

        Long userId = 1L;
        when(userService.delete(userId)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/users/delete/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(userId);
    }

    @Test
    @DisplayName("DELETE USER - Should handle service exception")
    void deleteUser_ServiceException() throws Exception {

        Long userId = 999L;
        when(userService.delete(userId))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(delete("/api/v1/users/delete/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is5xxServerError());

        verify(userService, times(1)).delete(userId);
    }

    @Test
    @DisplayName("CHANGE USER STATUS - Should change status to INACTIVE")
    void changeUserStatus() throws Exception {

        Long userId = 1L;
        StatusUser newStatus = StatusUser.INACTIVE;
        UserDTO.userResponse updatedUser = new UserDTO.userResponse(
                userId, "Juan Pérez", "juan@example.com", "3001234567",
                "DRIVER", "INACTIVE", testDateTime
        );

        when(userService.changeStatus(userId, newStatus)).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/v1/users/{id}/status", userId)
                        .param("status", newStatus.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.statusUser").value("INACTIVE"));

        verify(userService, times(1)).changeStatus(userId, newStatus);
    }

    @Test
    @DisplayName("CHANGE USER STATUS - Should change status to ACTIVE")
    void changeUserStatus_ToActive() throws Exception {

        Long userId = 2L;
        StatusUser newStatus = StatusUser.ACTIVE;
        UserDTO.userResponse updatedUser = new UserDTO.userResponse(
                userId, "María García", "maria@example.com", "3007777777",
                "CLERK", "ACTIVE", testDateTime
        );

        when(userService.changeStatus(userId, newStatus)).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/v1/users/{id}/status", userId)
                        .param("status", newStatus.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusUser").value("ACTIVE"));

        verify(userService, times(1)).changeStatus(userId, newStatus);
    }

    @Test
    @DisplayName("EXISTS BY EMAIL - Should return true when email exists")
    void existsByEmail() throws Exception {

        String email = "juan@example.com";
        when(userService.getByEmail(email)).thenReturn(null);

        mockMvc.perform(get("/api/v1/users/exists/email/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).getByEmail(email);
    }

    @Test
    @DisplayName("EXISTS BY EMAIL - Should return false when email doesn't exist")
    void existsByEmail_NotFound() throws Exception {

        String email = "new@example.com";
        when(userService.getByEmail(email)).thenReturn(null);

        mockMvc.perform(get("/api/v1/users/exists/email/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        verify(userService, times(1)).getByEmail(email);
    }

    @Test
    @DisplayName("GET BY PHONE (exists endpoint) - Should return true")
    void getByPhone() throws Exception {

        String phone = "3001234567";
        when(userService.getByPhone(phone)).thenReturn(null);

        mockMvc.perform(get("/api/v1/users/exists/phone/{phone}", phone)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).getByPhone(phone);
    }

    @Test
    @DisplayName("GET BY PHONE (exists endpoint) - Should return false")
    void getByPhone_NotFound() throws Exception {

        String phone = "3009999999";
        when(userService.getByPhone(phone)).thenReturn(null);

        mockMvc.perform(get("/api/v1/users/exists/phone/{phone}", phone)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        verify(userService, times(1)).getByPhone(phone);
    }

    @Test
    @DisplayName("CHECK AVAILABILITY - Should return both available")
    void checkAvailability() throws Exception {

        UserDTO.UserCheckRequest checkRequest = new UserDTO.UserCheckRequest(
                "newemail@example.com",
                "3005555555"
        );

        when(userService.getByEmail(checkRequest.email())).thenReturn(null);
        when(userService.getByPhone(checkRequest.phone())).thenReturn(null);

        mockMvc.perform(post("/api/v1/users/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailAvailable").value(true))
                .andExpect(jsonPath("$.phoneAvailable").value(true));

        verify(userService, times(1)).getByEmail(checkRequest.email());
        verify(userService, times(1)).getByPhone(checkRequest.phone());
    }

    @Test
    @DisplayName("CHECK AVAILABILITY - Should return email taken")
    void checkAvailability_EmailTaken() throws Exception {

        UserDTO.UserCheckRequest checkRequest = new UserDTO.UserCheckRequest(
                "existing@example.com",
                "3005555555"
        );

        when(userService.getByEmail(checkRequest.email())).thenReturn(null);
        when(userService.getByPhone(checkRequest.phone())).thenReturn(null);

        mockMvc.perform(post("/api/v1/users/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailAvailable").value(false))
                .andExpect(jsonPath("$.phoneAvailable").value(true));
    }

    @Test
    @DisplayName("CHECK AVAILABILITY - Should return phone taken")
    void checkAvailability_PhoneTaken() throws Exception {

        UserDTO.UserCheckRequest checkRequest = new UserDTO.UserCheckRequest(
                "newemail@example.com",
                "3001234567"
        );

        when(userService.getByEmail(checkRequest.email())).thenReturn(null);
        when(userService.getByPhone(checkRequest.phone())).thenReturn(null);

        mockMvc.perform(post("/api/v1/users/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailAvailable").value(true))
                .andExpect(jsonPath("$.phoneAvailable").value(false));
    }

    @Test
    @DisplayName("CHECK AVAILABILITY - Should return both taken")
    void checkAvailability_BothTaken() throws Exception {

        UserDTO.UserCheckRequest checkRequest = new UserDTO.UserCheckRequest(
                "existing@example.com",
                "3001234567"
        );

        when(userService.getByEmail(checkRequest.email())).thenReturn(null);
        when(userService.getByPhone(checkRequest.phone())).thenReturn(null);

        mockMvc.perform(post("/api/v1/users/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailAvailable").value(false))
                .andExpect(jsonPath("$.phoneAvailable").value(false));
    }

    @Test
    @DisplayName("UPDATE OWN PROFILE COMPLETE - Should update profile successfully")
    void updateOwnProfileComplete() throws Exception {

        UserDTO.userUpdateRequest updateRequest = new UserDTO.userUpdateRequest(
                "Juan Pérez Actualizado",
                "3009999999",
                StatusUser.ACTIVE
        );

        UserDTO.userResponse updatedResponse = new UserDTO.userResponse(
                1L,
                "Juan Pérez Actualizado",
                "juan.updated@example.com",
                "3009999999",
                "DRIVER",
                "ACTIVE",
                testDateTime
        );

        when(userService.update(any(UserDTO.userUpdateRequest.class), anyLong()))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/users/me/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print());

        UserDTO.userResponse result = userService.update(updateRequest, 1L);
        assertEquals("Juan Pérez Actualizado", result.name());
        assertEquals("3009999999", result.phone());
        assertEquals("juan.updated@example.com", result.email());
    }

    private void assertEquals(String expected, String actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }

    private void assertEquals(Long expected, Long actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }
}