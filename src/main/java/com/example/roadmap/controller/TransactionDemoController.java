package com.example.roadmap.controller;

import com.example.roadmap.dto.TransactionDemoRequestDto;
import com.example.roadmap.dto.TransactionDemoResultDto;
import com.example.roadmap.service.TransactionDemoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionDemoController {

  private final TransactionDemoService transactionDemoService;

  @PostMapping("/without-transactional")
  public TransactionDemoResultDto withoutTransactional(
      @Valid @RequestBody TransactionDemoRequestDto requestDto) {
    return transactionDemoService.runWithoutTransactional(requestDto);
  }

  @PostMapping("/with-transactional")
  public TransactionDemoResultDto withTransactional(
      @Valid @RequestBody TransactionDemoRequestDto requestDto) {
    return transactionDemoService.runWithTransactional(requestDto);
  }
}
