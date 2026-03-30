package com.example.roadmap.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.roadmap.exception.GlobalExceptionHandler;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerValidationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @Test
  void createShouldReturnUnifiedValidationError() throws Exception {
    String invalidPayload = """
        {
          "firstName": "",
          "lastName": "Mogilny",
          "email": "not-an-email"
        }
        """;

    mockMvc.perform(post("/api/users")
            .contentType(APPLICATION_JSON)
            .content(invalidPayload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.path").value("/api/users"))
        .andExpect(jsonPath("$.fieldErrors", hasSize(2)));
  }

  @Test
  void getByIdShouldReturnUnifiedNotFoundError() throws Exception {
    when(userService.getById(99L))
        .thenThrow(new ResourceNotFoundException("User with id=99 not found"));

    mockMvc.perform(get("/api/users/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("User with id=99 not found"))
        .andExpect(jsonPath("$.path").value("/api/users/99"));
  }

  @Test
  void getByIdShouldRejectNegativeIdentifier() throws Exception {
    mockMvc.perform(get("/api/users/-1"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.path").value("/api/users/-1"))
        .andExpect(jsonPath("$.fieldErrors", hasSize(1)));
  }

  @Test
  void createShouldReturnUnsupportedMediaType() throws Exception {
    mockMvc.perform(post("/api/users")
            .contentType(TEXT_PLAIN)
            .content("plain text"))
        .andExpect(status().isUnsupportedMediaType())
        .andExpect(jsonPath("$.status").value(415))
        .andExpect(jsonPath("$.error").value("Unsupported Media Type"))
        .andExpect(jsonPath("$.path").value("/api/users"));
  }

  @Test
  void patchCollectionShouldReturnMethodNotAllowed() throws Exception {
    mockMvc.perform(patch("/api/users"))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(jsonPath("$.status").value(405))
        .andExpect(jsonPath("$.error").value("Method Not Allowed"))
        .andExpect(jsonPath("$.path").value("/api/users"));
  }

  @Test
  void createShouldReturnConflictForDuplicateEmail() throws Exception {
    when(userService.create(org.mockito.ArgumentMatchers.any()))
        .thenThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint"));

    String validPayload = """
        {
          "firstName": "Ivan",
          "lastName": "Ivanov",
          "email": "same@example.com"
        }
        """;

    mockMvc.perform(post("/api/users")
            .contentType(APPLICATION_JSON)
            .content(validPayload))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.error").value("Conflict"))
        .andExpect(jsonPath("$.message")
            .value("Request conflicts with existing data or database constraints"))
        .andExpect(jsonPath("$.path").value("/api/users"));
  }
}
