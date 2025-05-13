package com.myapp.service;

import com.myapp.dto.UserDTO;
import com.myapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(Long id, UserDTO userDTO);

    void deleteUser(Long id);

    Optional<UserDTO> getUserById(Long id);

    Optional<UserDTO> getUserByUsername(String username);

    Optional<UserDTO> getUserByEmail(String email);

    Page<UserDTO> getAllUsers(Pageable pageable);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    UserDTO convertToDTO(User user);

    User convertToEntity(UserDTO userDTO);

    User findByUsername(String username);

    void updateLastLogin(String username);
}
