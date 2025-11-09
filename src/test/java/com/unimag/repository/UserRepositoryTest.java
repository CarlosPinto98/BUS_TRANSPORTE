package com.unimag.repository;

import com.unimag.AbstractRepositoryTest;
import com.unimag.entities.Enums.Role;
import com.unimag.entities.Enums.StatusUser;
import com.unimag.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Encontrar user por email")
    void findByEmail() {

        var user =  User.builder()
                .name("Maria Garcia")
                .email("maria@example.com")
                .phone("3009876543")
                .role(Role.PASSENGER)
                .statusUser(StatusUser.ACTIVE)
                .passwordHash("hash")
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("maria@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Maria Garcia");
    }

    @Test
    @DisplayName("Buscar user por phone")
    void findByPhone() {

        var user = User.builder()
                .name("Pedro Lopez")
                .email("pedro@example.com")
                .phone("3112223344")
                .role(Role.DRIVER)
                .statusUser(StatusUser.ACTIVE)
                .passwordHash("hash")
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByPhone("3112223344");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("pedro@example.com");
    }

    @Test
    @DisplayName("Verificar si email existe")
    void existsByEmail() {
        var user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .phone("3001111111")
                .role(Role.PASSENGER)
                .statusUser(StatusUser.ACTIVE)
                .passwordHash("hash")
                .build();
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("test@example.com");
        boolean notExists = userRepository.existsByEmail("otro@example.com");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Verificar si existe phone")
    void existsByPhone() {

        var user = User.builder()
                .name("Phone User")
                .email("phone@example.com")
                .phone("3002222222")
                .role(Role.CLERK)
                .statusUser(StatusUser.ACTIVE)
                .passwordHash("hash")
                .build();
        userRepository.save(user);
        boolean exists = userRepository.existsByPhone("3002222222");
        boolean notExists = userRepository.existsByPhone("3009999999");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Listar user por role")
    void findByRole() {

        var passenger1 = User.builder()
                .name("Passenger 1")
                .email("p1@example.com")
                .phone("3001111111")
                .role(Role.PASSENGER)
                .statusUser(StatusUser.ACTIVE)
                .passwordHash("hash")
                .build();

        var passenger2 = User.builder()
                .name("Passenger 2")
                .email("p2@example.com")
                .phone("3002222222")
                .role(Role.PASSENGER)
                .statusUser(StatusUser.ACTIVE)
                .passwordHash("hash")
                .build();

        var driver = User.builder()
                .name("Driver")
                .email("driver@example.com")
                .phone("3003333333")
                .role(Role.DRIVER)
                .statusUser(StatusUser.ACTIVE)
                .passwordHash("hash")
                .build();

        userRepository.saveAll(List.of(passenger1, passenger2, driver));

        List<User> passengers = userRepository.findByRole(Role.PASSENGER);
        List<User> drivers = userRepository.findByRole(Role.DRIVER);

        assertThat(passengers).hasSize(2);
        assertThat(drivers).hasSize(1);
        assertThat(drivers.get(0).getEmail()).isEqualTo("driver@example.com");
    }

    @Test
    @DisplayName("Listar user por role y estado")
    void findByRoleAndStatusUser() {

        var activePassenger = User.builder()
                .name("Active Passenger")
                .email("active@example.com")
                .phone("3001111111")
                .role(Role.PASSENGER)
                .statusUser(StatusUser.ACTIVE)
                .passwordHash("hash")
                .build();

        var inactivePassenger = User.builder()
                .name("Inactive Passenge")
                .email("inactive@example.com")
                .phone("3002222222")
                .role(Role.PASSENGER)
                .statusUser(StatusUser.INACTIVE)
                .passwordHash("hash")
                .build();

        userRepository.saveAll(List.of(activePassenger, inactivePassenger));

        List<User> activeUsers = userRepository.findByRoleAndStatusUser(
                Role.PASSENGER,StatusUser.ACTIVE);
        List<User> inactiveUsers = userRepository.findByRoleAndStatusUser(
                Role.PASSENGER, StatusUser.INACTIVE);

        assertThat(activeUsers).hasSize(1);
        assertThat(inactiveUsers).hasSize(1);
        assertThat(activeUsers.get(0).getEmail()).isEqualTo("active@example.com");
    }

    @Test
    @DisplayName("Buscar email no existente")
    void shouldNotFindUserByNonExistentEmail() {
        Optional<User> found = userRepository.findByEmail("noexiste@example.com");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Guardar un User")
    void shouldSaveUser() {
        var user = User.builder()
                .name("Juan Pérez")
                .email("juan@example.com")
                .phone("3001234567")
                .role(Role.PASSENGER)
                .statusUser(StatusUser.ACTIVE)
                .passwordHash("hashedPassword123")
                .build();

        User saved = userRepository.save(user);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Juan Pérez");
        assertThat(saved.getStatusUser()).isEqualTo(StatusUser.ACTIVE);
        assertThat(saved.getCreateAt()).isNotNull();
    }

    @Test
    @DisplayName("Encontrar por nombre")
    void findByName() {

        Optional<User> found = userRepository.findByName("john_doe");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("john_doe");
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }
}

