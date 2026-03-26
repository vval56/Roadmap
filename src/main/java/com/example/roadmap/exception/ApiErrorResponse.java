package com.example.roadmap.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Unified error response for all API endpoints")
public record ApiErrorResponse(
    @Schema(description = "Error timestamp in ISO-8601 format",
        example = "2026-03-25T16:00:00+03:00")
    OffsetDateTime timestamp,
    @Schema(description = "HTTP status code", example = "400")
    int status,
    @Schema(description = "HTTP reason phrase", example = "Bad Request")
    String error,
    @Schema(description = "High-level error message", example = "Validation failed")
    String message,
    @Schema(description = "Request path that caused the error", example = "/api/users")
    String path,
    @ArraySchema(arraySchema = @Schema(description = "Detailed validation errors"))
    List<ApiFieldError> fieldErrors
) {
}
