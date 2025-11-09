package com.unimag.repository;

import com.unimag.entities.Enums.Role;
import com.unimag.entities.Enums.StatusUser;
import com.unimag.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<User> findByRole(Role role);

    List<User> findByRoleAndStatusUser(Role role, StatusUser statusUser);

    Optional<User> findByName(String name);
}
