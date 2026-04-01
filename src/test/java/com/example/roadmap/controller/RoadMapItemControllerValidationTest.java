package com.example.roadmap.controller;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.roadmap.exception.BusinessRuleViolationException;
import com.example.roadmap.exception.GlobalExceptionHandler;
import com.example.roadmap.service.RoadMapItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RoadMapItemController.class)
@Import(GlobalExceptionHandler.class)
class RoadMapItemControllerValidationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private RoadMapItemService roadMapItemService;

  @Test
  void updateShouldReturnBadRequestForBackwardStatusTransition() throws Exception {
    when(roadMapItemService.update(org.mockito.ArgumentMatchers.eq(37L), org.mockito.ArgumentMatchers.any()))
        .thenThrow(new BusinessRuleViolationException(
            "RoadMapItem status cannot move backward from IN_PROGRESS to PLANNED"));

    String payload = """
        {
          "title": "Learn JPA basics",
          "details": "Entity mapping, repositories, relationships",
          "status": "PLANNED",
          "roadMapId": 2,
          "parentItemId": null,
          "tagIds": []
        }
        """;

    mockMvc.perform(put("/api/roadmap-items/37")
            .contentType(APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.message")
            .value("RoadMapItem status cannot move backward from IN_PROGRESS to PLANNED"))
        .andExpect(jsonPath("$.path").value("/api/roadmap-items/37"));
  }
}
