package com.example.roadmap.controller;

import com.example.roadmap.dto.TransactionDemoRequestDto;
import com.example.roadmap.dto.TransactionDemoResultDto;
import com.example.roadmap.service.TransactionDemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transactional behaviour demonstration endpoints")
public class TransactionDemoController {

  private final TransactionDemoService transactionDemoService;

  @PostMapping("/without-transactional")
  @Operation(summary = "Run bulk demo without @Transactional",
      description = "Creates a roadmap and starts bulk roadmap-item insertion without a single wrapping transaction")
  public TransactionDemoResultDto withoutTransactional(
      @Valid @RequestBody TransactionDemoRequestDto requestDto) {
    return transactionDemoService.runWithoutTransactional(requestDto);
  }

  @PostMapping("/with-transactional")
  @Operation(summary = "Run bulk demo with @Transactional",
      description = "Creates a roadmap and starts bulk roadmap-item insertion inside one transaction")
  public TransactionDemoResultDto withTransactional(
      @Valid @RequestBody TransactionDemoRequestDto requestDto) {
    return transactionDemoService.runWithTransactional(requestDto);
  }
}
