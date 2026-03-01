package com.example.roadmap.service;

import com.example.roadmap.dto.TransactionDemoRequestDto;

/**
 * Worker service for transaction demonstrations.
 */
public interface TransactionWorkerService {

  void saveWithoutTransactionalAndFail(TransactionDemoRequestDto requestDto);

  void saveWithTransactionalAndFail(TransactionDemoRequestDto requestDto);
}
