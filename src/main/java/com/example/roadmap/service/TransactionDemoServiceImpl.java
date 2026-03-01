package com.example.roadmap.service;

import com.example.roadmap.dto.TransactionDemoRequestDto;
import com.example.roadmap.dto.TransactionDemoResultDto;
import com.example.roadmap.repository.RoadMapItemRepository;
import com.example.roadmap.repository.RoadMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * TransactionDemoServiceImpl component.
 */
@Service
@RequiredArgsConstructor
public class TransactionDemoServiceImpl implements TransactionDemoService {

  private final TransactionWorkerService transactionWorkerService;
  private final RoadMapRepository roadMapRepository;
  private final RoadMapItemRepository roadMapItemRepository;

  @Override
  public TransactionDemoResultDto runWithoutTransactional(TransactionDemoRequestDto requestDto) {
    TransactionDemoResultDto result = createBeforeSnapshot();
    try {
      transactionWorkerService.saveWithoutTransactionalAndFail(requestDto);
      result.setMessage("No exception happened");
    } catch (RuntimeException ex) {
      result.setMessage(ex.getMessage());
    }
    fillAfterSnapshot(result);
    return result;
  }

  @Override
  public TransactionDemoResultDto runWithTransactional(TransactionDemoRequestDto requestDto) {
    TransactionDemoResultDto result = createBeforeSnapshot();
    try {
      transactionWorkerService.saveWithTransactionalAndFail(requestDto);
      result.setMessage("No exception happened");
    } catch (RuntimeException ex) {
      result.setMessage(ex.getMessage());
    }
    fillAfterSnapshot(result);
    return result;
  }

  private TransactionDemoResultDto createBeforeSnapshot() {
    TransactionDemoResultDto result = new TransactionDemoResultDto();
    result.setRoadMapsBefore(roadMapRepository.count());
    result.setItemsBefore(roadMapItemRepository.count());
    return result;
  }

  private void fillAfterSnapshot(TransactionDemoResultDto result) {
    result.setRoadMapsAfter(roadMapRepository.count());
    result.setItemsAfter(roadMapItemRepository.count());
  }
}
