package com.unimag.service;

import com.unimag.DTO.UserDTO;
import com.unimag.DTO.UserDTO.userResponse;
import com.unimag.entities.Enums.Role;
import com.unimag.entities.Enums.StatusUser;
import com.unimag.entities.User;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.UserMapper;
import com.unimag.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@Transactional
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public userResponse update(UserDTO.userUpdateRequest userUpdateRequest, Long id) {
        var f = getObject(id);
        userMapper.updateEntity(userUpdateRequest,f);
        return userMapper.toResponse(f);
    }

    @Override
    public userResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    @Override
    public userResponse getByPhone(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("User not found with phone: " + phone));
        return userMapper.toResponse(user);
    }

    @Override
    public userResponse changeStatus(Long id, StatusUser status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        user.setStatusUser(status);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    public userResponse create(UserDTO.userCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }
        if (userRepository.existsByPhone(request.phone())) {
            throw new IllegalArgumentException("Phone already exists: " + request.phone());
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(request.password());
        user.setStatusUser(StatusUser.ACTIVE);

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public userResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    public userResponse save(UserDTO.userCreateRequest userCreateRequest) {
        var entity = userMapper.toEntity(userCreateRequest);
        entity.setCreateAt(OffsetDateTime.now().toLocalDateTime());
        return userMapper.toResponse(userRepository.save(entity));
    }

    @Override
    public userResponse get(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("user not found"));
        return userMapper.toResponse(user);
    }

    @Override
    public Page<userResponse> getAll(Pageable pageable) {
        var users = userRepository.findAll(pageable);
        return users.map(userMapper::toResponse);
    }

    @Override
    public userResponse getAssigments(Long id) {
        var s = getObject(id);
        return userMapper.toResponse(s);
    }

    @Override
    public boolean delete(Long id) {
        var f = getObject(id);
        boolean check = false;
        if (f != null) {
            userRepository.deleteById(id);
            check = true;
        }
        return check;
    }



    @Override
    public User getObject(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("user not found"));
    }

    public User getObject(Long id, Role role) {
        var s = getObject(id);
        if (s.getRole().equals(role)) {
            return s;
        }
        throw new NotFoundException("user with that rol does not exist");
    }

}