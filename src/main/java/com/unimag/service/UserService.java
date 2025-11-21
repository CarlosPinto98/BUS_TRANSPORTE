package com.unimag.service;

import com.unimag.DTO.UserDTO;
import com.unimag.entities.Enums.StatusUser;
import com.unimag.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserDTO.userResponse create(UserDTO.userCreateRequest request);
    UserDTO.userResponse save(UserDTO.userCreateRequest userCreateRequest);
    UserDTO.userResponse get(Long id);
    Page<UserDTO.userResponse> getAll(Pageable pageable);
    UserDTO.userResponse getAssigments(Long id);
    boolean delete(Long id);
    UserDTO.userResponse update(UserDTO.userUpdateRequest userUpdateRequest, Long id);
    UserDTO.userResponse getById(Long id);
    User getObject(Long id);
    UserDTO.userResponse getByEmail(String email);
    UserDTO.userResponse getByPhone(String phone);
    UserDTO.userResponse changeStatus(Long id, StatusUser status);

    //UserDTO.userResponse update(UserDTO.@Valid userUpdateRequest request, Long userId);
}
