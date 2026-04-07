package com.example.roadmap.service;

import com.example.roadmap.dto.RoadMapAnalyticsReportDto;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.model.ItemStatus;
import com.example.roadmap.model.RoadMap;
import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.repository.RoadMapRepository;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoadMapAnalyticsAsyncWorker {

  private final RoadMapRepository roadMapRepository;
  private final AsyncTaskRegistryService asyncTaskRegistryService;

  @Async("roadMapAsyncExecutor")
  @Transactional(readOnly = true)
  public CompletableFuture<Void> generateReportAsync(String taskId, Long roadMapId) {
    asyncTaskRegistryService.markRunning(taskId);

    try {
      TimeUnit.MILLISECONDS.sleep(400);

      RoadMap roadMap = roadMapRepository.findDetailedById(roadMapId)
          .orElseThrow(() -> new ResourceNotFoundException("RoadMap with id=" + roadMapId + " not found"));

      asyncTaskRegistryService.complete(taskId, buildReport(roadMap));
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      asyncTaskRegistryService.fail(taskId, "Async report generation was interrupted");
    } catch (RuntimeException ex) {
      asyncTaskRegistryService.fail(taskId, resolveFailureMessage(ex));
    }

    return CompletableFuture.completedFuture(null);
  }

  private RoadMapAnalyticsReportDto buildReport(RoadMap roadMap) {
    int totalItems = roadMap.getItems().size();
    int plannedItems = countItemsByStatus(roadMap, ItemStatus.PLANNED);
    int inProgressItems = countItemsByStatus(roadMap, ItemStatus.IN_PROGRESS);
    int doneItems = countItemsByStatus(roadMap, ItemStatus.DONE);
    int totalComments = roadMap.getItems().stream()
        .mapToInt(item -> item.getComments().size())
        .sum();

    RoadMapAnalyticsReportDto report = new RoadMapAnalyticsReportDto();
    report.setRoadMapId(roadMap.getId());
    report.setRoadMapTitle(roadMap.getTitle());
    report.setOwnerEmail(roadMap.getOwner().getEmail());
    report.setTotalItems(totalItems);
    report.setPlannedItems(plannedItems);
    report.setInProgressItems(inProgressItems);
    report.setDoneItems(doneItems);
    report.setTotalComments(totalComments);
    report.setCompletionRatePercent(totalItems == 0 ? 0.0 : doneItems * 100.0 / totalItems);
    report.setDistinctTagNames(roadMap.getItems().stream()
        .flatMap(item -> item.getTags().stream())
        .map(tag -> tag.getName().trim())
        .distinct()
        .sorted()
        .toList());
    report.setGeneratedAt(OffsetDateTime.now());
    return report;
  }

  private int countItemsByStatus(RoadMap roadMap, ItemStatus status) {
    return Math.toIntExact(roadMap.getItems().stream()
        .map(RoadMapItem::getStatus)
        .filter(status::equals)
        .count());
  }

  private String resolveFailureMessage(RuntimeException ex) {
    return ex.getMessage() == null || ex.getMessage().isBlank()
        ? "Async report generation failed"
        : ex.getMessage();
  }
}
