package com.hotel.huespedes.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema()
public class ErrorResponse {

    @Schema()
    private boolean success;

    @Schema()
    private int status;

    @Schema()
    private String error;

    @Schema()
    private String message;

    @Schema()
    private String path;

    @Schema()
    private Map<String, String> validationErrors;

    @Builder.Default
    @Schema()
    private LocalDateTime timestamp = LocalDateTime.now();
}
