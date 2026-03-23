package com.example.roadmap.service;

import com.example.roadmap.dto.TransactionDemoRequestDto;
import com.example.roadmap.dto.TransactionDemoResultDto;

public interface TransactionDemoService {

  TransactionDemoResultDto runWithoutTransactional(TransactionDemoRequestDto requestDto);

  TransactionDemoResultDto runWithTransactional(TransactionDemoRequestDto requestDto);
}
