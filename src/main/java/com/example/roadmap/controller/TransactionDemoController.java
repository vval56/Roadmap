package com.example.roadmap.controller;

import com.example.roadmap.dto.TransactionDemoRequestDto;
import com.example.roadmap.dto.TransactionDemoResultDto;
import com.example.roadmap.service.TransactionDemoService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

  private static final String BULK_FAILURE_REQUEST_EXAMPLE = """
      {
        "ownerId": 1,
        "roadMapTitle": "Lab 5 Bulk Transaction Demo",
        "items": [
          {
            "title": "Valid item",
            "details": "This item should be saved before the failure",
            "status": "PLANNED",
            "tagIds": []
          },
          {
            "title": "Broken item",
            "details": "This item references a missing tag and breaks the bulk operation",
            "status": "IN_PROGRESS",
            "tagIds": [999999]
          }
        ]
      }
      """;

  private static final String WITHOUT_TRANSACTIONAL_RESPONSE_EXAMPLE = """
      {
        "roadMapsBefore": 10,
        "roadMapsAfter": 11,
        "itemsBefore": 20,
        "itemsAfter": 21,
        "transactional": false,
        "requestedItems": 2,
        "message": "Tag with id=999999 not found"
      }
      """;

  private static final String WITH_TRANSACTIONAL_RESPONSE_EXAMPLE = """
      {
        "roadMapsBefore": 10,
        "roadMapsAfter": 10,
        "itemsBefore": 20,
        "itemsAfter": 20,
        "transactional": true,
        "requestedItems": 2,
        "message": "Tag with id=999999 not found"
      }
      """;

  private final TransactionDemoService transactionDemoService;

  @PostMapping("/without-transactional")
  @Operation(summary = "Run bulk demo without @Transactional",
      description = "Send two bulk items: the first is valid, the second contains a missing tag id. "
          + "Without one wrapping transaction the roadmap and the first item remain persisted.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Bulk operation result without rollback",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = TransactionDemoResultDto.class),
              examples = @ExampleObject(
                  name = "WithoutTransactionalResult",
                  value = WITHOUT_TRANSACTIONAL_RESPONSE_EXAMPLE)))
  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      description = "Bulk request where the first item is valid and the second item is invalid",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = TransactionDemoRequestDto.class),
          examples = @ExampleObject(
              name = "BulkWithBrokenSecondItem",
              value = BULK_FAILURE_REQUEST_EXAMPLE)))
  public TransactionDemoResultDto withoutTransactional(
      @Valid @RequestBody TransactionDemoRequestDto requestDto) {
    return transactionDemoService.runWithoutTransactional(requestDto);
  }

  @PostMapping("/with-transactional")
  @Operation(summary = "Run bulk demo with @Transactional",
      description = "Send the same two bulk items: the first is valid, the second contains a missing tag id. "
          + "Inside one transaction both the roadmap and the first item are rolled back.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Bulk operation result with rollback",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = TransactionDemoResultDto.class),
              examples = @ExampleObject(
                  name = "WithTransactionalResult",
                  value = WITH_TRANSACTIONAL_RESPONSE_EXAMPLE)))
  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      description = "Bulk request where the first item is valid and the second item is invalid",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = TransactionDemoRequestDto.class),
          examples = @ExampleObject(
              name = "BulkWithBrokenSecondItem",
              value = BULK_FAILURE_REQUEST_EXAMPLE)))
  public TransactionDemoResultDto withTransactional(
      @Valid @RequestBody TransactionDemoRequestDto requestDto) {
    return transactionDemoService.runWithTransactional(requestDto);
  }
}
