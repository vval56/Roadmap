package com.example.roadmap.controller;

import com.example.roadmap.dto.RaceConditionDemoRequestDto;
import com.example.roadmap.dto.RaceConditionDemoResultDto;
import com.example.roadmap.service.ConcurrencyDemoService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/concurrency")
@RequiredArgsConstructor
@Tag(name = "Concurrency", description = "Race condition and thread-safety demonstration endpoints")
public class ConcurrencyDemoController {

  private static final String RESPONSE_EXAMPLE = """
      {
        "threadCount": 64,
        "incrementsPerThread": 5000,
        "expectedValue": 320000,
        "unsafeCounterValue": 241532,
        "synchronizedCounterValue": 320000,
        "atomicCounterValue": 320000,
        "raceConditionDetected": true,
        "durationMs": 184
      }
      """;

  private final ConcurrencyDemoService concurrencyDemoService;

  @PostMapping("/race-condition")
  @Operation(summary = "Run race condition demo",
      description = "Starts 50+ threads in ExecutorService, compares unsafe counter with synchronized and AtomicInteger solutions")
  @ApiResponse(responseCode = "200", description = "Race condition experiment completed",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = RaceConditionDemoResultDto.class),
          examples = @ExampleObject(
              name = "RaceConditionDetected",
              value = RESPONSE_EXAMPLE)))
  @ApiResponse(responseCode = "400", description = "Invalid thread or increment count", content = @Content)
  public RaceConditionDemoResultDto runRaceConditionDemo(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          description = "Experiment parameters. The unsafe counter should produce a value smaller than expected, "
              + "while synchronized and AtomicInteger counters should match the expected value exactly.",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = """
                  {
                    "threadCount": 64,
                    "incrementsPerThread": 5000
                  }
                  """)))
      @Valid @RequestBody RaceConditionDemoRequestDto requestDto) {
    return concurrencyDemoService.runRaceConditionDemo(requestDto);
  }
}
