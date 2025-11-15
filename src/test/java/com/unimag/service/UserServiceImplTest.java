package com.unimag.service;

import com.unimag.DTO.UserDTO;
import com.unimag.DTO.UserDTO.*;
import com.unimag.entities.Enums.Role;
import com.unimag.entities.Enums.StatusUser;
import com.unimag.entities.User;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.UserMapper;
import com.unimag.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private userCreateRequest createRequest;
    private userUpdateRequest updateRequest;
    private userResponse response;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .name("Juan Perez")
                .email("juan@example.com")
                .phone("3001234567")
                .passwordHash("hashedPassword123")
                .role(Role.PASSENGER)
                .statusUser(StatusUser.ACTIVE)
                .createAt(LocalDateTime.now())
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .seatHolds(new HashSet<>())
                .build();

        createRequest = new UserDTO.userCreateRequest(
                "Juan Perez",
                "juan@example.com",
                "3001234567",
                Role.PASSENGER,
                "password123"
        );

        updateRequest = new UserDTO.userUpdateRequest(
                "Juan Perez Actualizado",
                "3009876543",
                StatusUser.INACTIVE
        );

        response = new UserDTO.userResponse(
                1L,
                "Juan Perez",
                "juan@example.com",
                "3001234567",
                "PASSENGER",
                "ACTIVE",
                LocalDateTime.now()
        );
    }


    @Test
    @DisplayName("Save - Debe guardar un usuario exitosamente")
    void save() {

        when(userMapper.toEntity(createRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserDTO.userResponse result = userService.save(createRequest);

        assertNotNull(result);
        assertTrue(result.email().contains("@"));
        assertTrue(result.email().matches("^[A-Za-z0-9+_.-]+@(.+)$"));
    }

    @Test
    @DisplayName("Save - Debe establecer createAt automáticamente")
    void saveSetCreateAtAutomatically() {

        when(userMapper.toEntity(createRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserDTO.userResponse result = userService.save(createRequest);

        assertNotNull(result);
        assertNotNull(result.createAt());

        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Save - Debe validar formato de email")
    void saveValidateEmailFormat() {

        when(userMapper.toEntity(createRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserDTO.userResponse result = userService.save(createRequest);

        assertNotNull(result);
        assertTrue(result.email().contains("@"));
        assertTrue(result.email().matches("^[A-Za-z0-9+_.-]+@(.+)$"));
    }

    @Test
    @DisplayName("Save - Debe validar teléfono de 10 dígitos")
    void saveValidatePhoneFormat() {

        when(userMapper.toEntity(createRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserDTO.userResponse result = userService.save(createRequest);

        assertNotNull(result);
        assertEquals(10, result.phone().length());
        assertTrue(result.phone().matches("\\d{10}"));
    }

    @Test
    @DisplayName("Save - Debe guardar usuario con rol PASSENGER")
    void saveSaveUserWithPassengerRole() {

        when(userMapper.toEntity(createRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserDTO.userResponse result = userService.save(createRequest);

        assertNotNull(result);
        assertEquals("PASSENGER", result.role());
    }

    @Test
    @DisplayName("Save - Debe guardar usuario con rol DRIVER")
    void saveUserWithDriverRole() {

        UserDTO.userCreateRequest driverRequest =
                new UserDTO.userCreateRequest(
                        "Carlos Rodriguez", "carlos@example.com",
                        "3005555555", Role.DRIVER, "password123"
                );

        User driver = User.builder()
                .id(2L)
                .name("Carlos Rodriguez")
                .email("carlos@example.com")
                .phone("3005555555")
                .role(Role.DRIVER)
                .statusUser(StatusUser.ACTIVE)
                .createAt(LocalDateTime.now())
                .build();

        UserDTO.userResponse driverResponse =
                new UserDTO.userResponse(2L, "Carlos Rodriguez", "carlos@example.com",
                        "3005555555", "DRIVER", "ACTIVE", LocalDateTime.now());

        when(userMapper.toEntity(driverRequest)).thenReturn(driver);
        when(userRepository.save(driver)).thenReturn(driver);
        when(userMapper.toResponse(driver)).thenReturn(driverResponse);

        UserDTO.userResponse result = userService.save(driverRequest);

        assertNotNull(result);
        assertEquals("DRIVER", result.role());
    }


    @Test
    @DisplayName("Get - Debe obtener un usuario por ID")
    void get() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserDTO.userResponse result = userService.get(1L);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.id());
        assertEquals("Juan Perez", result.name());
        assertEquals("juan@example.com", result.email());

        verify(userRepository).findById(1L);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("Get - Debe lanzar excepción cuando el ID no existe")
    void getThrowException_WhenIdNotFound() {

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.get(999L)
        );

        assertEquals("user not found", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("GetAll - Debe retornar página de usuarios")
    void getAll() {

        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = List.of(user);
        Page<User> userPage = new PageImpl<>(users, pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toResponse(user)).thenReturn(response);

        Page<UserDTO.userResponse> result = userService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(response.id(), result.getContent().get(0).id());

        verify(userRepository).findAll(pageable);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("GetAll - Debe retornar página vacía cuando no hay usuarios")
    void getAllReturnEmptyPage_WhenNoUsers() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<UserDTO.userResponse> result = userService.getAll(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(userRepository).findAll(pageable);
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("GetAssignments - Debe obtener asignaciones del usuario")
    void getAssigments() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserDTO.userResponse result = userService.getAssigments(1L);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.id());

        verify(userRepository).findById(1L);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("GetAssignments - Debe lanzar excepción cuando el usuario no existe")
    void getThrowException_WhenUserNotFound() {

        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getAssigments(999L));
        verify(userRepository).findById(999L);
    }


    @Test
    @DisplayName("Delete - Debe eliminar usuario cuando existe")
    void delete() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        boolean result = userService.delete(1L);

        assertTrue(result);
        verify(userRepository).findById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete - Debe lanzar excepción cuando el usuario no existe")
    void deleteThrowException_WhenUserNotFound() {

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete(999L));

        verify(userRepository).findById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Update - Debe actualizar usuario exitosamente")
    void update() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateEntity(updateRequest, user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserDTO.userResponse result = userService.update(updateRequest, 1L);

        assertNotNull(result);
        assertEquals(response.id(), result.id());

        verify(userRepository).findById(1L);
        verify(userMapper).updateEntity(updateRequest, user);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("Update - Debe actualizar nombre del usuario")
    void UpdateUserName() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doAnswer(invocation -> {
            User u = invocation.getArgument(1);
            u.setName("Juan Perez Actualizado");
            return null;
        }).when(userMapper).updateEntity(updateRequest, user);

        UserDTO.userResponse updatedResponse =
                new UserDTO.userResponse(1L, "Juan Perez Actualizado", "juan@example.com",
                        "3009876543", "PASSENGER", "INACTIVE", LocalDateTime.now());
        when(userMapper.toResponse(user)).thenReturn(updatedResponse);

        UserDTO.userResponse result = userService.update(updateRequest, 1L);

        assertNotNull(result);
        assertEquals("Juan Perez Actualizado", result.name());
    }

    @Test
    @DisplayName("Update - Debe actualizar teléfono del usuario")
    void updateUserPhone() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doAnswer(invocation -> {
            User u = invocation.getArgument(1);
            u.setPhone("3009876543");
            return null;
        }).when(userMapper).updateEntity(updateRequest, user);

        UserDTO.userResponse updatedResponse =
                new UserDTO.userResponse(1L, "Juan Perez", "juan@example.com",
                        "3009876543", "PASSENGER", "INACTIVE", LocalDateTime.now());
        when(userMapper.toResponse(user)).thenReturn(updatedResponse);

        UserDTO.userResponse result = userService.update(updateRequest, 1L);

        assertNotNull(result);
        assertEquals("3009876543", result.phone());
    }

    @Test
    @DisplayName("Update - Debe actualizar status del usuario")
    void updateUserStatus() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doAnswer(invocation -> {
            User u = invocation.getArgument(1);
            u.setStatusUser(StatusUser.INACTIVE);
            return null;
        }).when(userMapper).updateEntity(updateRequest, user);

        UserDTO.userResponse updatedResponse =
                new UserDTO.userResponse(1L, "Juan Perez", "juan@example.com",
                        "3001234567", "PASSENGER", "INACTIVE", LocalDateTime.now());
        when(userMapper.toResponse(user)).thenReturn(updatedResponse);

        UserDTO.userResponse result = userService.update(updateRequest, 1L);

        assertNotNull(result);
        assertEquals("INACTIVE", result.statusUser());
    }

    @Test
    @DisplayName("Update - Debe lanzar excepción cuando el usuario no existe")
    void updateThrowException_WhenUserNotFound() {

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.update(updateRequest, 999L)
        );

        assertEquals("user not found", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(userMapper, never()).updateEntity(any(), any());
    }

    @Test
    @DisplayName("GetObject - Debe retornar entidad User cuando existe")
    void getObject() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getObject(1L);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
        assertEquals("Juan Perez", result.getName());
        assertEquals("juan@example.com", result.getEmail());
        assertEquals("3001234567", result.getPhone());
        assertEquals(Role.PASSENGER, result.getRole());

        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("GetObject - Debe lanzar excepción cuando no existe")
    void getObjectThrowException_WhenNotExists() {

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getObject(999L)
        );

        assertEquals("user not found", exception.getMessage());
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("GetObject with Role - Debe retornar usuario con rol específico")
    void testGetObject() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getObject(1L, Role.PASSENGER);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
        assertEquals(Role.PASSENGER, result.getRole());

        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("GetObject with Role - Debe lanzar excepción cuando el rol no coincide")
    void testGetObjectThrowException_WhenRoleDoesNotMatch() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getObject(1L, Role.DRIVER)
        );

        assertEquals("user with that rol does not exist", exception.getMessage());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("GetObject with Role - Debe funcionar con rol DRIVER")
    void testGetObjectWork_WithDriverRole() {

        User driver = User.builder()
                .id(2L)
                .name("Carlos Rodriguez")
                .email("carlos@example.com")
                .phone("3005555555")
                .role(Role.DRIVER)
                .statusUser(StatusUser.ACTIVE)
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(driver));

        User result = userService.getObject(2L, Role.DRIVER);

        assertNotNull(result);
        assertEquals(Long.valueOf(2L), result.getId());
        assertEquals(Role.DRIVER, result.getRole());
    }

    @Test
    @DisplayName("GetObject with Role - Debe funcionar con rol DISPATCHER")
    void testGetObjectWork_WithDispatcherRole() {

        User dispatcher = User.builder()
                .id(3L)
                .name("Ana Martinez")
                .email("ana@example.com")
                .phone("3007777777")
                .role(Role.DISPATCHER)
                .statusUser(StatusUser.ACTIVE)
                .build();

        when(userRepository.findById(3L)).thenReturn(Optional.of(dispatcher));

        User result = userService.getObject(3L, Role.DISPATCHER);

        assertNotNull(result);
        assertEquals(Long.valueOf(3L), result.getId());
        assertEquals(Role.DISPATCHER, result.getRole());
    }

    @Test
    @DisplayName("Save - Debe guardar usuario con status ACTIVE por defecto")
    void saveUserWithActiveStatusByDefault() {

        when(userMapper.toEntity(createRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserDTO.userResponse result = userService.save(createRequest);

        assertNotNull(result);
        assertEquals("ACTIVE", result.statusUser());
    }

    @Test
    @DisplayName("User - Debe calcular edad correctamente")
    void userCalculateAgeCorrectly() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getObject(1L);
        int age = result.getAge();

        assertTrue(age >= 0);
        assertTrue(age < 95);
    }
}