package com.example.roadmap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.roadmap.dto.RaceConditionDemoRequestDto;
import com.example.roadmap.dto.RaceConditionDemoResultDto;
import org.junit.jupiter.api.Test;

class ConcurrencyDemoServiceTest {

  private final ConcurrencyDemoService concurrencyDemoService = new ConcurrencyDemoService();

  @Test
  void shouldExposeRaceConditionAndSafeCounterSolution() {
    RaceConditionDemoRequestDto requestDto = new RaceConditionDemoRequestDto();
    requestDto.setThreadCount(64);
    requestDto.setIncrementsPerThread(2000);

    RaceConditionDemoResultDto result = concurrencyDemoService.runRaceConditionDemo(requestDto);

    assertEquals(128000, result.getExpectedValue());
    assertEquals(result.getExpectedValue(), result.getSynchronizedCounterValue());
    assertEquals(result.getExpectedValue(), result.getAtomicCounterValue());
    assertTrue(result.isRaceConditionDetected());
    assertTrue(result.getUnsafeCounterValue() < result.getExpectedValue());
  }
}
