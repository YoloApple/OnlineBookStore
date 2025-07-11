package com.example.bookstore.service;

import com.example.bookstore.dto.UserResponse;
import com.example.bookstore.entity.Role;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.RoleRepository;
import com.example.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    public void updateUserInfo(Long userId, String roleName,String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (roleName != null && !roleName.isBlank()) {
            Role.ERole newRoleEnum;
            switch (roleName.toLowerCase()) {
                case "admin":
                    newRoleEnum = Role.ERole.ROLE_ADMIN;
                    break;
                case "seller":
                    newRoleEnum = Role.ERole.ROLE_SELLER;
                    break;
                default:
                    newRoleEnum = Role.ERole.ROLE_USER;
            }

            Role newRole = roleRepository.findByName(newRoleEnum)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền"));

            Set<Role> newRoles = new HashSet<>();
            newRoles.add(newRole);
            user.setRoles(newRoles);
        }

        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        userRepository.save(user);
    }


    public boolean deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) return false;
        userRepository.deleteById(userId);
        return true;
    }
}

