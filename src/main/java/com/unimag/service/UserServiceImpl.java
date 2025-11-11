package com.unimag.service;

import com.unimag.DTO.UserDTO;
import com.unimag.entities.Enums.Role;
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
    public UserDTO.userResponse save(UserDTO.userCreateRequest userCreateRequest) {
        var entity = userMapper.toEntity(userCreateRequest);
        entity.setCreateAt(OffsetDateTime.now().toLocalDateTime());
        return userMapper.toResponse(userRepository.save(entity));
    }

    @Override
    public UserDTO.userResponse get(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("user not found"));
        return userMapper.toResponse(user);
    }

    @Override
    public Page<UserDTO.userResponse> getAll(Pageable pageable) {
        var users = userRepository.findAll(pageable);
        return users.map(userMapper::toResponse);
    }

    @Override
    public UserDTO.userResponse getAssigments(Long id) {
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
    public UserDTO.userResponse update(UserDTO.userUpdateRequest userUpdateRequest, Long id) {
        var f = getObject(id);
        userMapper.updateEntity(userUpdateRequest, f);
        return userMapper.toResponse(f);
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