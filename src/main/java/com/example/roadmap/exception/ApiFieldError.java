package com.example.roadmap.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Validation error details for a specific field or parameter")
public record ApiFieldError(
    @Schema(description = "Field or parameter name", example = "email")
    String field,
    @Schema(description = "Validation message", example = "must be a well-formed email address")
    String message,
    @Schema(description = "Rejected value", example = "not-an-email")
    Object rejectedValue
) {
}
