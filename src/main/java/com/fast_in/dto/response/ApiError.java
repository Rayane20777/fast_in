package com.fast_in.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "API Error Response")
public class ApiError {
    @Schema(description = "Timestamp when the error occurred")
    private LocalDateTime timestamp;
    
    @Schema(description = "HTTP status code")
    private int status;
    
    @Schema(description = "Error code")
    private String code;
    
    @Schema(description = "Error message")
    private String message;
    
    @Schema(description = "Request path")
    private String path;
    
    @Schema(description = "Additional error details")
    private Map<String, ?> details;
}