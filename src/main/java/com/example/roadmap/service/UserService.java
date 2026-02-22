package com.example.roadmap.service;

import com.example.roadmap.model.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User getUserByUsername(String username);
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
}