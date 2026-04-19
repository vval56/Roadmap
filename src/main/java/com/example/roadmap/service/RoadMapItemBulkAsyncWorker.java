package com.example.roadmap.service;

import com.example.roadmap.dto.AsyncRoadMapItemBulkResultDto;
import com.example.roadmap.dto.RoadMapItemBulkCreateDto;
import com.example.roadmap.dto.RoadMapItemDto;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoadMapItemBulkAsyncWorker {

  private final RoadMapItemService roadMapItemService;
  private final AsyncTaskRegistryService asyncTaskRegistryService;

  @Value("${app.async.bulk-create-delay-ms:5000}")
  private long bulkCreateDelayMs;

  @Async("roadMapAsyncExecutor")
  public CompletableFuture<Void> createBulkAsync(String taskId, Long roadMapId,
                                                 List<RoadMapItemBulkCreateDto> dtos) {
    asyncTaskRegistryService.markRunning(taskId);

    try {
      waitForDemonstrationDelay();
      List<RoadMapItemDto> saved = roadMapItemService.createBulk(roadMapId, dtos);
      asyncTaskRegistryService.completeBulk(taskId, buildResult(roadMapId, saved));
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      asyncTaskRegistryService.fail(taskId, "Async roadmap item bulk creation was interrupted");
    } catch (RuntimeException ex) {
      asyncTaskRegistryService.fail(taskId, resolveFailureMessage(ex));
    }

    return CompletableFuture.completedFuture(null);
  }

  private void waitForDemonstrationDelay() throws InterruptedException {
    if (bulkCreateDelayMs <= 0) {
      if (Thread.currentThread().isInterrupted()) {
        throw new InterruptedException();
      }
      return;
    }
    TimeUnit.MILLISECONDS.sleep(bulkCreateDelayMs);
  }

  private AsyncRoadMapItemBulkResultDto buildResult(Long roadMapId, List<RoadMapItemDto> saved) {
    AsyncRoadMapItemBulkResultDto result = new AsyncRoadMapItemBulkResultDto();
    result.setRoadMapId(roadMapId);
    result.setCreatedItemsCount(saved.size());
    result.setCreatedItemIds(saved.stream().map(RoadMapItemDto::getId).toList());
    result.setFinishedAt(OffsetDateTime.now());
    return result;
  }

  private String resolveFailureMessage(RuntimeException ex) {
    return ex.getMessage() == null || ex.getMessage().isBlank()
        ? "Async roadmap item bulk creation failed"
        : ex.getMessage();
  }
}
