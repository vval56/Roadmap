package com.example.roadmap.service;

import com.example.roadmap.dto.AuthLoginRequestDto;
import com.example.roadmap.dto.AuthLoginResponseDto;
import com.example.roadmap.dto.AuthRegisterRequestDto;
import com.example.roadmap.dto.UserDto;
import com.example.roadmap.dto.UserMapper;
import com.example.roadmap.exception.AuthenticationFailedException;
import com.example.roadmap.exception.BusinessRuleViolationException;
import com.example.roadmap.model.User;
import com.example.roadmap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;

  @Override
  public AuthLoginResponseDto login(AuthLoginRequestDto request) {
    String normalizedLogin = request.getLogin().trim();
    String normalizedPassword = request.getPassword().trim();

    User user = userRepository.findByLoginIgnoreCase(normalizedLogin)
        .or(() -> userRepository.findByEmailIgnoreCase(normalizedLogin))
        .orElseThrow(() -> new AuthenticationFailedException("Invalid login or password"));

    if (user.getPassword() == null || !user.getPassword().equals(normalizedPassword)) {
      throw new AuthenticationFailedException("Invalid login or password");
    }

    UserDto userDto = UserMapper.toDto(user);
    return new AuthLoginResponseDto("Signed in", userDto);
  }

  @Override
  @Transactional
  public AuthLoginResponseDto register(AuthRegisterRequestDto request) {
    String normalizedEmail = request.getEmail().trim().toLowerCase();
    String normalizedLogin = request.getLogin().trim();

    if (userRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
      throw new BusinessRuleViolationException("Email is already registered");
    }

    if (userRepository.findByLoginIgnoreCase(normalizedLogin).isPresent()) {
      throw new BusinessRuleViolationException("Login is already taken");
    }

    User user = new User();
    user.setFirstName(request.getFirstName().trim());
    user.setLastName(request.getLastName().trim());
    user.setEmail(normalizedEmail);
    user.setLogin(normalizedLogin);
    user.setPassword(request.getPassword().trim());

    User saved = userRepository.save(user);
    return new AuthLoginResponseDto("Registered and signed in", UserMapper.toDto(saved));
  }
}
