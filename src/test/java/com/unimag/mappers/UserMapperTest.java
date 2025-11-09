package com.unimag.mappers;

import com.unimag.DTO.UserDTO.*;
import com.unimag.entities.Enums.Role;
import com.unimag.entities.Enums.StatusUser;
import com.unimag.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserMapper Tests")
class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    @DisplayName("Debe mapear UserCreateRequest a la entidad User")
    void toEntity() {

        UserCreateRequest request = new UserCreateRequest(
                "John Doe",
                "john.doe@example.com",
                "3001234567",
                Role.PASSENGER,
                "password123"
        );

        User user = userMapper.toEntity(request);

        System.out.println("===== DEBUG =====");
        System.out.println("Name: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Phone: " + user.getPhone());
        System.out.println("Role: " + user.getRole());
        System.out.println("StatusUser: " + user.getStatusUser());
        System.out.println("ID: " + user.getId());
        System.out.println("PasswordHash: " + user.getPasswordHash());
        System.out.println("CreateAt: " + user.getCreateAt());
        System.out.println("=================");

        assertNotNull(user);
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("3001234567", user.getPhone());
        assertEquals(Role.PASSENGER, user.getRole());
        assertNull(user.getId());
        assertNull(user.getPasswordHash());
        assertNotNull(user.getCreateAt());
    }

    @Test
    @DisplayName("Debe actualizar la entidad User desde UserUpdateRequest")
    void updateEntity() {

        User existingUser = User.builder()
                .id(1L)
                .name("Old Name")
                .email("old@example.com")
                .phone("3009999999")
                .role(Role.PASSENGER)
                .statusUser(StatusUser.ACTIVE)
                .passwordHash("hashedpass")
                .createAt(LocalDateTime.now())
                .build();

        UserUpdateRequest request = new UserUpdateRequest(
                "New Name",
                "3001111111",
                StatusUser.INACTIVE
        );
        userMapper.updateEntity(request, existingUser);

        assertEquals("New Name", existingUser.getName());
        assertEquals("3001111111", existingUser.getPhone());
        assertEquals(StatusUser.INACTIVE, existingUser.getStatusUser());

        assertEquals("old@example.com", existingUser.getEmail());
        assertEquals(Role.PASSENGER, existingUser.getRole());
    }

    @Test
    @DisplayName("Debe mapear la entidad User a UserResponse")
    void toResponse() {

        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("3001234567")
                .role(Role.CLERK)
                .statusUser(StatusUser.ACTIVE)
                .passwordHash("hashedpass")
                .createAt(now)
                .build();

        UserResponse response = userMapper.toResponse(user);


        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("John Doe", response.name());
        assertEquals("john.doe@example.com", response.email());
        assertEquals("3001234567", response.phone());
        assertEquals("CLERK", response.role());
        assertEquals("ACTIVE", response.statusUser());
        assertEquals(now, response.createAt());
    }

    @Test
    @DisplayName("Debe manejar valores nulos en UserCreateRequest")
    void shouldHandleNullValuesInCreateRequest() {
        // Given
        UserCreateRequest request = new UserCreateRequest(
                null,
                null,
                null,
                null,
                null
        );

        User user = userMapper.toEntity(request);

        assertNotNull(user);
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPhone());
        assertNull(user.getRole());
    }

    @Test
    @DisplayName("Debe mapear todos los tipos de UserRole correctamente")
    void shouldMapAllUserRoles() {
        for (Role role : Role.values()) {
            UserCreateRequest request = new UserCreateRequest(
                    "Test User",
                    "test@example.com",
                    "3001234567",
                    role,
                    "password"
            );

            User user = userMapper.toEntity(request);
            assertEquals(role, user.getRole());

            user.setId(1L);
            user.setCreateAt(LocalDateTime.now());
            user.setPasswordHash("hash");

            UserResponse response = userMapper.toResponse(user);
            assertEquals(role.name(), response.role());
        }
    }

    @Test
    @DisplayName("Debe mapear todos los tipos de UserStatus correctamente")
    void shouldMapAllUserStatuses() {
        for (StatusUser status : StatusUser.values()) {
            User user = User.builder()
                    .id(1L)
                    .name("Test")
                    .email("test@example.com")
                    .phone("3001234567")
                    .role(Role.PASSENGER)
                    .statusUser(status)
                    .passwordHash("hash")
                    .createAt(LocalDateTime.now())
                    .build();

            UserResponse response = userMapper.toResponse(user);
            assertEquals(status.name(), response.statusUser());
        }
    }
}