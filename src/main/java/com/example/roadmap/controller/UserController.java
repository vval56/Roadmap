package com.example.roadmap.controller;

import com.example.roadmap.dto.UserDto;
import com.example.roadmap.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "Users", description = "CRUD operations for users")
public class UserController {

  private final UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create user")
  public UserDto create(@Valid @RequestBody UserDto dto) {
    return userService.create(dto);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get user by id")
  public UserDto getById(@Parameter(description = "User id", example = "1")
                         @PathVariable @Positive Long id) {
    return userService.getById(id);
  }

  @GetMapping
  @Operation(summary = "Get all users")
  public List<UserDto> getAll() {
    return userService.getAll();
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update user", description = "Replaces user data by id")
  public UserDto update(@Parameter(description = "User id", example = "1")
                        @PathVariable @Positive Long id,
                        @Valid @RequestBody UserDto dto) {
    return userService.update(id, dto);
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Patch user")
  public UserDto patch(@Parameter(description = "User id", example = "1")
                       @PathVariable @Positive Long id,
                       @Valid @RequestBody UserDto dto) {
    return userService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete user")
  public void delete(@Parameter(description = "User id", example = "1")
                     @PathVariable @Positive Long id) {
    userService.delete(id);
  }
}
