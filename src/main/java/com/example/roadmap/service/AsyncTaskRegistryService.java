package com.example.roadmap.service;

import com.example.roadmap.dto.AsyncTaskCountersDto;
import com.example.roadmap.dto.AsyncRoadMapItemBulkResultDto;
import com.example.roadmap.dto.AsyncTaskStatus;
import com.example.roadmap.dto.AsyncTaskStatusDto;
import com.example.roadmap.dto.AsyncTaskType;
import com.example.roadmap.dto.RoadMapAnalyticsReportDto;
import com.example.roadmap.exception.ResourceNotFoundException;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class AsyncTaskRegistryService {

  private final ConcurrentMap<String, AsyncTaskState> tasks = new ConcurrentHashMap<>();
  private final AtomicLong submittedTasks = new AtomicLong();
  private final AtomicLong runningTasks = new AtomicLong();
  private final AtomicLong completedTasks = new AtomicLong();
  private final AtomicLong failedTasks = new AtomicLong();

  public String registerRoadMapReportTask(Long roadMapId) {
    return registerTask(AsyncTaskType.ROADMAP_ANALYTICS_REPORT, roadMapId);
  }

  public String registerRoadMapItemBulkTask(Long roadMapId) {
    return registerTask(AsyncTaskType.ROADMAP_ITEM_BULK_CREATE, roadMapId);
  }

  private String registerTask(AsyncTaskType taskType, Long roadMapId) {
    String taskId = UUID.randomUUID().toString();
    tasks.put(taskId, AsyncTaskState.pending(taskId, taskType, roadMapId));
    submittedTasks.incrementAndGet();
    return taskId;
  }

  public void markRunning(String taskId) {
    AsyncTaskState task = getTask(taskId);
    if (task.markRunning()) {
      runningTasks.incrementAndGet();
    }
  }

  public void complete(String taskId, RoadMapAnalyticsReportDto report) {
    AsyncTaskState task = getTask(taskId);
    FinishResult finishResult = task.markCompleted(report);
    if (finishResult.finished()) {
      if (finishResult.decrementRunning()) {
        runningTasks.decrementAndGet();
      }
      completedTasks.incrementAndGet();
    }
  }

  public void completeBulk(String taskId, AsyncRoadMapItemBulkResultDto bulkResult) {
    AsyncTaskState task = getTask(taskId);
    FinishResult finishResult = task.markCompleted(bulkResult);
    if (finishResult.finished()) {
      if (finishResult.decrementRunning()) {
        runningTasks.decrementAndGet();
      }
      completedTasks.incrementAndGet();
    }
  }

  public void fail(String taskId, String errorMessage) {
    AsyncTaskState task = getTask(taskId);
    FinishResult finishResult = task.markFailed(errorMessage);
    if (finishResult.finished()) {
      if (finishResult.decrementRunning()) {
        runningTasks.decrementAndGet();
      }
      failedTasks.incrementAndGet();
    }
  }

  public AsyncTaskStatusDto getStatus(String taskId) {
    return getTask(taskId).toDto();
  }

  public AsyncTaskCountersDto getCounters() {
    return new AsyncTaskCountersDto(
        submittedTasks.get(),
        runningTasks.get(),
        completedTasks.get(),
        failedTasks.get()
    );
  }

  private AsyncTaskState getTask(String taskId) {
    AsyncTaskState task = tasks.get(taskId);
    if (task == null) {
      throw new ResourceNotFoundException("Async task with id=" + taskId + " not found");
    }
    return task;
  }

  private static final class AsyncTaskState {
    private final String taskId;
    private final AsyncTaskType taskType;
    private final Long roadMapId;
    private final OffsetDateTime createdAt;
    private AsyncTaskStatus status;
    private OffsetDateTime startedAt;
    private OffsetDateTime completedAt;
    private String errorMessage;
    private RoadMapAnalyticsReportDto report;
    private AsyncRoadMapItemBulkResultDto bulkResult;

    private AsyncTaskState(String taskId, AsyncTaskType taskType, Long roadMapId,
                           OffsetDateTime createdAt, AsyncTaskStatus status) {
      this.taskId = taskId;
      this.taskType = taskType;
      this.roadMapId = roadMapId;
      this.createdAt = createdAt;
      this.status = status;
    }

    static AsyncTaskState pending(String taskId, AsyncTaskType taskType, Long roadMapId) {
      return new AsyncTaskState(taskId, taskType, roadMapId, OffsetDateTime.now(), AsyncTaskStatus.PENDING);
    }

    synchronized boolean markRunning() {
      if (status != AsyncTaskStatus.PENDING) {
        return false;
      }
      status = AsyncTaskStatus.RUNNING;
      startedAt = OffsetDateTime.now();
      return true;
    }

    synchronized FinishResult markCompleted(RoadMapAnalyticsReportDto completedReport) {
      if (status == AsyncTaskStatus.COMPLETED || status == AsyncTaskStatus.FAILED) {
        return FinishResult.NO_CHANGE;
      }
      boolean decrementRunning = status == AsyncTaskStatus.RUNNING;
      if (startedAt == null) {
        startedAt = OffsetDateTime.now();
      }
      status = AsyncTaskStatus.COMPLETED;
      completedAt = OffsetDateTime.now();
      report = completedReport;
      bulkResult = null;
      errorMessage = null;
      return FinishResult.finished(decrementRunning);
    }

    synchronized FinishResult markCompleted(AsyncRoadMapItemBulkResultDto completedBulkResult) {
      if (status == AsyncTaskStatus.COMPLETED || status == AsyncTaskStatus.FAILED) {
        return FinishResult.NO_CHANGE;
      }
      boolean decrementRunning = status == AsyncTaskStatus.RUNNING;
      if (startedAt == null) {
        startedAt = OffsetDateTime.now();
      }
      status = AsyncTaskStatus.COMPLETED;
      completedAt = OffsetDateTime.now();
      bulkResult = completedBulkResult;
      report = null;
      errorMessage = null;
      return FinishResult.finished(decrementRunning);
    }

    synchronized FinishResult markFailed(String failureMessage) {
      if (status == AsyncTaskStatus.COMPLETED || status == AsyncTaskStatus.FAILED) {
        return FinishResult.NO_CHANGE;
      }
      boolean decrementRunning = status == AsyncTaskStatus.RUNNING;
      if (startedAt == null) {
        startedAt = OffsetDateTime.now();
      }
      status = AsyncTaskStatus.FAILED;
      completedAt = OffsetDateTime.now();
      errorMessage = failureMessage;
      report = null;
      bulkResult = null;
      return FinishResult.finished(decrementRunning);
    }

    synchronized AsyncTaskStatusDto toDto() {
      return new AsyncTaskStatusDto(
          taskId,
          taskType,
          roadMapId,
          status,
          createdAt,
          startedAt,
          completedAt,
          errorMessage,
          report,
          bulkResult
      );
    }
  }

  private record FinishResult(boolean finished, boolean decrementRunning) {
    private static final FinishResult NO_CHANGE = new FinishResult(false, false);

    private static FinishResult finished(boolean decrementRunning) {
      return new FinishResult(true, decrementRunning);
    }
  }
}
