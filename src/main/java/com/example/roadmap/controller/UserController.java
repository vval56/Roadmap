package com.example.roadmap.controller;

import com.example.roadmap.dto.UserDTO;
import com.example.roadmap.dto.UserMapper;
import com.example.roadmap.model.User;
import com.example.roadmap.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = service.getAllUsers();
        List<UserDTO> dtos = UserMapper.toDTOList(users);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = service.getUserById(id);
        return user != null ? ResponseEntity.ok(UserMapper.toDTO(user)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        User user = service.getUserByUsername(username);
        return user != null ? ResponseEntity.ok(UserMapper.toDTO(user)) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO dto) {
        User entity = UserMapper.toEntity(dto);
        User created = service.createUser(entity);
        return ResponseEntity.ok(UserMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO dto) {
        User entity = UserMapper.toEntity(dto);
        User updated = service.updateUser(id, entity);
        return updated != null ? ResponseEntity.ok(UserMapper.toDTO(updated)) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}