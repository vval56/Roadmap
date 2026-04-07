package com.example.roadmap.service;

import com.example.roadmap.dto.RaceConditionDemoRequestDto;
import com.example.roadmap.dto.RaceConditionDemoResultDto;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class ConcurrencyDemoService {

  private static final int MAX_ATTEMPTS = 5;

  public RaceConditionDemoResultDto runRaceConditionDemo(RaceConditionDemoRequestDto requestDto) {
    int threadCount = requestDto.getThreadCount();
    int incrementsPerThread = requestDto.getIncrementsPerThread();
    RaceConditionDemoResultDto lastResult = null;

    for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
      lastResult = runSingleExperiment(threadCount, incrementsPerThread);
      if (lastResult.isRaceConditionDetected()) {
        return lastResult;
      }
    }

    return lastResult;
  }

  private RaceConditionDemoResultDto runSingleExperiment(int threadCount, int incrementsPerThread) {
    int expectedValue = threadCount * incrementsPerThread;
    UnsafeCounter unsafeCounter = new UnsafeCounter();
    SynchronizedCounter synchronizedCounter = new SynchronizedCounter();
    AtomicInteger atomicCounter = new AtomicInteger();
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    CountDownLatch readyLatch = new CountDownLatch(threadCount);
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(threadCount);

    for (int index = 0; index < threadCount; index++) {
      executorService.submit(() -> runCounterTask(
          incrementsPerThread, unsafeCounter, synchronizedCounter, atomicCounter,
          readyLatch, startLatch, doneLatch));
    }

    long startedAt = System.nanoTime();

    try {
      readyLatch.await();
      startLatch.countDown();
      doneLatch.await();
      executorService.shutdown();
      executorService.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Race condition demo was interrupted", ex);
    } finally {
      executorService.shutdownNow();
    }

    RaceConditionDemoResultDto result = new RaceConditionDemoResultDto();
    result.setThreadCount(threadCount);
    result.setIncrementsPerThread(incrementsPerThread);
    result.setExpectedValue(expectedValue);
    result.setUnsafeCounterValue(unsafeCounter.getValue());
    result.setSynchronizedCounterValue(synchronizedCounter.getValue());
    result.setAtomicCounterValue(atomicCounter.get());
    result.setRaceConditionDetected(unsafeCounter.getValue() != expectedValue);
    result.setDurationMs(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt));
    return result;
  }

  private void runCounterTask(int incrementsPerThread, UnsafeCounter unsafeCounter,
                              SynchronizedCounter synchronizedCounter, AtomicInteger atomicCounter,
                              CountDownLatch readyLatch, CountDownLatch startLatch,
                              CountDownLatch doneLatch) {
    readyLatch.countDown();
    try {
      startLatch.await();
      for (int increment = 0; increment < incrementsPerThread; increment++) {
        unsafeCounter.increment();
        synchronizedCounter.increment();
        atomicCounter.incrementAndGet();
      }
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    } finally {
      doneLatch.countDown();
    }
  }

  private static final class UnsafeCounter {
    private int value;

    private void increment() {
      int snapshot = value;
      for (int index = 0; index < 8; index++) {
        Thread.onSpinWait();
      }
      Thread.yield();
      value = snapshot + 1;
    }

    private int getValue() {
      return value;
    }
  }

  private static final class SynchronizedCounter {
    private int value;

    private synchronized void increment() {
      value++;
    }

    private synchronized int getValue() {
      return value;
    }
  }
}
