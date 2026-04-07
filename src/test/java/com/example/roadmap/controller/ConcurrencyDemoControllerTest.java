package com.example.roadmap.controller;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.roadmap.dto.RaceConditionDemoResultDto;
import com.example.roadmap.exception.GlobalExceptionHandler;
import com.example.roadmap.service.ConcurrencyDemoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ConcurrencyDemoController.class)
@Import(GlobalExceptionHandler.class)
class ConcurrencyDemoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ConcurrencyDemoService concurrencyDemoService;

  @Test
  void runRaceConditionDemoShouldReturnExperimentResult() throws Exception {
    RaceConditionDemoResultDto result = new RaceConditionDemoResultDto();
    result.setThreadCount(64);
    result.setIncrementsPerThread(5000);
    result.setExpectedValue(320000);
    result.setUnsafeCounterValue(241532);
    result.setSynchronizedCounterValue(320000);
    result.setAtomicCounterValue(320000);
    result.setRaceConditionDetected(true);
    result.setDurationMs(184);

    when(concurrencyDemoService.runRaceConditionDemo(org.mockito.ArgumentMatchers.any()))
        .thenReturn(result);

    String payload = """
        {
          "threadCount": 64,
          "incrementsPerThread": 5000
        }
        """;

    mockMvc.perform(post("/api/concurrency/race-condition")
            .contentType(APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.expectedValue").value(320000))
        .andExpect(jsonPath("$.unsafeCounterValue").value(241532))
        .andExpect(jsonPath("$.synchronizedCounterValue").value(320000))
        .andExpect(jsonPath("$.atomicCounterValue").value(320000))
        .andExpect(jsonPath("$.raceConditionDetected").value(true));
  }

  @Test
  void runRaceConditionDemoShouldRejectTooSmallThreadCount() throws Exception {
    String payload = """
        {
          "threadCount": 10,
          "incrementsPerThread": 5000
        }
        """;

    mockMvc.perform(post("/api/concurrency/race-condition")
            .contentType(APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.path").value("/api/concurrency/race-condition"));
  }
}
