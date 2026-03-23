package com.example.roadmap.service;

import com.example.roadmap.dto.TransactionDemoRequestDto;

public interface TransactionWorkerService {

  void saveWithoutTransactionalAndFail(TransactionDemoRequestDto requestDto);

  void saveWithTransactionalAndFail(TransactionDemoRequestDto requestDto);
}
