package com.example.roadmap.service;

import com.example.roadmap.cache.RoadMapItemSearchIndexService;
import com.example.roadmap.dto.TransactionDemoRequestDto;
import com.example.roadmap.dto.TransactionDemoResultDto;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.repository.RoadMapItemRepository;
import com.example.roadmap.repository.RoadMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionDemoServiceImpl implements TransactionDemoService {

  private final TransactionWorkerService transactionWorkerService;
  private final RoadMapRepository roadMapRepository;
  private final RoadMapItemRepository roadMapItemRepository;
  private final RoadMapItemSearchIndexService searchIndexService;

  @Override
  public TransactionDemoResultDto runWithoutTransactional(TransactionDemoRequestDto requestDto) {
    return runDemo(requestDto, false);
  }

  @Override
  public TransactionDemoResultDto runWithTransactional(TransactionDemoRequestDto requestDto) {
    return runDemo(requestDto, true);
  }

  private TransactionDemoResultDto runDemo(TransactionDemoRequestDto requestDto,
                                           boolean transactional) {
    TransactionDemoResultDto result = createBeforeSnapshot();
    result.setTransactional(transactional);
    result.setRequestedItems(requestDto.getItems().size());

    try {
      if (transactional) {
        transactionWorkerService.saveWithTransactionalAndFail(requestDto);
      } else {
        transactionWorkerService.saveWithoutTransactionalAndFail(requestDto);
      }
      result.setMessage("Bulk operation completed without exception");
    } catch (ResourceNotFoundException ex) {
      result.setMessage(ex.getMessage());
    }

    fillAfterSnapshot(result);
    searchIndexService.invalidateAll();
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
