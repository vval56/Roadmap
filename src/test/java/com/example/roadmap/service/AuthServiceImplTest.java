package com.example.roadmap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.roadmap.dto.AuthLoginRequestDto;
import com.example.roadmap.dto.AuthRegisterRequestDto;
import com.example.roadmap.exception.AuthenticationFailedException;
import com.example.roadmap.exception.BusinessRuleViolationException;
import com.example.roadmap.model.User;
import com.example.roadmap.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private AuthServiceImpl authService;

  @Test
  void loginShouldAuthenticateByLogin() {
    AuthLoginRequestDto request = new AuthLoginRequestDto();
    request.setLogin(" admin ");
    request.setPassword(" admin123 ");

    User user = user("admin", "admin123");
    user.setId(1L);
    when(userRepository.findByLoginIgnoreCase("admin")).thenReturn(Optional.of(user));

    var response = authService.login(request);

    assertEquals("Signed in", response.getMessage());
    assertEquals(1L, response.getUser().getId());
    assertEquals("admin", response.getUser().getLogin());
    assertEquals("vladislav@example.com", response.getUser().getEmail());
  }

  @Test
  void loginShouldFallbackToEmailLookup() {
    AuthLoginRequestDto request = new AuthLoginRequestDto();
    request.setLogin("vladislav@example.com");
    request.setPassword("admin123");

    User user = user("admin", "admin123");
    user.setId(2L);
    when(userRepository.findByLoginIgnoreCase("vladislav@example.com")).thenReturn(Optional.empty());
    when(userRepository.findByEmailIgnoreCase("vladislav@example.com")).thenReturn(Optional.of(user));

    var response = authService.login(request);

    assertEquals("Signed in", response.getMessage());
    assertEquals(2L, response.getUser().getId());
  }

  @Test
  void loginShouldFailWhenUserNotFound() {
    AuthLoginRequestDto request = new AuthLoginRequestDto();
    request.setLogin("missing");
    request.setPassword("secret");
    when(userRepository.findByLoginIgnoreCase("missing")).thenReturn(Optional.empty());
    when(userRepository.findByEmailIgnoreCase("missing")).thenReturn(Optional.empty());

    AuthenticationFailedException exception = assertThrows(AuthenticationFailedException.class,
        () -> authService.login(request));

    assertEquals("Invalid login or password", exception.getMessage());
  }

  @Test
  void loginShouldFailWhenPasswordIsNull() {
    AuthLoginRequestDto request = new AuthLoginRequestDto();
    request.setLogin("admin");
    request.setPassword("admin123");

    User user = user("admin", null);
    when(userRepository.findByLoginIgnoreCase("admin")).thenReturn(Optional.of(user));

    AuthenticationFailedException exception = assertThrows(AuthenticationFailedException.class,
        () -> authService.login(request));

    assertEquals("Invalid login or password", exception.getMessage());
  }

  @Test
  void loginShouldFailWhenPasswordDoesNotMatch() {
    AuthLoginRequestDto request = new AuthLoginRequestDto();
    request.setLogin("admin");
    request.setPassword("wrong");

    User user = user("admin", "admin123");
    when(userRepository.findByLoginIgnoreCase("admin")).thenReturn(Optional.of(user));

    AuthenticationFailedException exception = assertThrows(AuthenticationFailedException.class,
        () -> authService.login(request));

    assertEquals("Invalid login or password", exception.getMessage());
  }

  @Test
  void registerShouldCreateUserWithNormalizedFields() {
    AuthRegisterRequestDto request = new AuthRegisterRequestDto();
    request.setFirstName("  Vladislav ");
    request.setLastName("  Mogilny ");
    request.setEmail("  VLADISLAV@EXAMPLE.COM ");
    request.setLogin("  admin  ");
    request.setPassword("  admin123  ");

    when(userRepository.findByEmailIgnoreCase("vladislav@example.com")).thenReturn(Optional.empty());
    when(userRepository.findByLoginIgnoreCase("admin")).thenReturn(Optional.empty());

    User saved = user("admin", "admin123");
    saved.setId(10L);
    saved.setFirstName("Vladislav");
    saved.setLastName("Mogilny");
    when(userRepository.save(any(User.class))).thenReturn(saved);

    var response = authService.register(request);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());
    User persisted = captor.getValue();
    assertEquals("Vladislav", persisted.getFirstName());
    assertEquals("Mogilny", persisted.getLastName());
    assertEquals("vladislav@example.com", persisted.getEmail());
    assertEquals("admin", persisted.getLogin());
    assertEquals("admin123", persisted.getPassword());

    assertEquals("Registered and signed in", response.getMessage());
    assertEquals(10L, response.getUser().getId());
    assertEquals("admin", response.getUser().getLogin());
  }

  @Test
  void registerShouldFailWhenEmailAlreadyExists() {
    AuthRegisterRequestDto request = new AuthRegisterRequestDto();
    request.setFirstName("A");
    request.setLastName("B");
    request.setEmail("user@example.com");
    request.setLogin("user");
    request.setPassword("user123");

    when(userRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(new User()));

    BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class,
        () -> authService.register(request));

    assertEquals("Email is already registered", exception.getMessage());
    verify(userRepository, never()).findByLoginIgnoreCase(any());
    verify(userRepository, never()).save(any());
  }

  @Test
  void registerShouldFailWhenLoginAlreadyExists() {
    AuthRegisterRequestDto request = new AuthRegisterRequestDto();
    request.setFirstName("A");
    request.setLastName("B");
    request.setEmail("user@example.com");
    request.setLogin("user");
    request.setPassword("user123");

    when(userRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.empty());
    when(userRepository.findByLoginIgnoreCase("user")).thenReturn(Optional.of(new User()));

    BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class,
        () -> authService.register(request));

    assertEquals("Login is already taken", exception.getMessage());
    verify(userRepository, never()).save(any());
  }

  private User user(String login, String password) {
    User user = new User();
    user.setFirstName("Vladislav");
    user.setLastName("Mogilny");
    user.setEmail("vladislav@example.com");
    user.setLogin(login);
    user.setPassword(password);
    return user;
  }
}
