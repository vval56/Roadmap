package com.example.roadmap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.roadmap.dto.RaceConditionDemoRequestDto;
import com.example.roadmap.dto.RaceConditionDemoResultDto;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
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

  @Test
  void shouldReturnLastResultWhenRaceConditionDoesNotOccur() {
    RaceConditionDemoRequestDto requestDto = new RaceConditionDemoRequestDto();
    requestDto.setThreadCount(1);
    requestDto.setIncrementsPerThread(10);

    RaceConditionDemoResultDto result = concurrencyDemoService.runRaceConditionDemo(requestDto);

    assertEquals(10, result.getExpectedValue());
    assertEquals(10, result.getUnsafeCounterValue());
    assertEquals(10, result.getSynchronizedCounterValue());
    assertEquals(10, result.getAtomicCounterValue());
    assertFalse(result.isRaceConditionDetected());
  }

  @Test
  void shouldThrowIllegalStateExceptionWhenExperimentThreadIsInterrupted() throws Exception {
    Method method = ConcurrencyDemoService.class.getDeclaredMethod("runSingleExperiment", int.class, int.class);
    method.setAccessible(true);

    Thread.currentThread().interrupt();
    try {
      InvocationTargetException exception = org.junit.jupiter.api.Assertions.assertThrows(InvocationTargetException.class,
          () -> method.invoke(concurrencyDemoService, 2, 2));
      assertInstanceOf(IllegalStateException.class, exception.getCause());
      assertEquals("Race condition demo was interrupted", exception.getCause().getMessage());
    } finally {
      Thread.interrupted();
    }
  }

  @Test
  void shouldStopCounterTaskGracefullyWhenInterruptedBeforeStart() throws Exception {
    Method method = ConcurrencyDemoService.class.getDeclaredMethod(
        "runCounterTask",
        int.class,
        Class.forName("com.example.roadmap.service.ConcurrencyDemoService$UnsafeCounter"),
        Class.forName("com.example.roadmap.service.ConcurrencyDemoService$SynchronizedCounter"),
        AtomicInteger.class,
        CountDownLatch.class,
        CountDownLatch.class,
        CountDownLatch.class
    );
    method.setAccessible(true);

    Class<?> unsafeCounterClass = Class.forName("com.example.roadmap.service.ConcurrencyDemoService$UnsafeCounter");
    Class<?> synchronizedCounterClass = Class.forName("com.example.roadmap.service.ConcurrencyDemoService$SynchronizedCounter");
    java.lang.reflect.Constructor<?> unsafeConstructor = unsafeCounterClass.getDeclaredConstructor();
    unsafeConstructor.setAccessible(true);
    java.lang.reflect.Constructor<?> synchronizedConstructor = synchronizedCounterClass.getDeclaredConstructor();
    synchronizedConstructor.setAccessible(true);
    Object unsafeCounter = unsafeConstructor.newInstance();
    Object synchronizedCounter = synchronizedConstructor.newInstance();
    AtomicInteger atomicCounter = new AtomicInteger();
    CountDownLatch readyLatch = new CountDownLatch(1);
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(1);

    Thread.currentThread().interrupt();
    try {
      method.invoke(concurrencyDemoService, 5, unsafeCounter, synchronizedCounter, atomicCounter,
          readyLatch, startLatch, doneLatch);
    } finally {
      Thread.interrupted();
    }

    Method unsafeGetValue = unsafeCounterClass.getDeclaredMethod("getValue");
    unsafeGetValue.setAccessible(true);
    Method synchronizedGetValue = synchronizedCounterClass.getDeclaredMethod("getValue");
    synchronizedGetValue.setAccessible(true);
    assertEquals(0L, readyLatch.getCount());
    assertEquals(0L, doneLatch.getCount());
    assertEquals(0, unsafeGetValue.invoke(unsafeCounter));
    assertEquals(0, synchronizedGetValue.invoke(synchronizedCounter));
    assertEquals(0, atomicCounter.get());
  }
}
