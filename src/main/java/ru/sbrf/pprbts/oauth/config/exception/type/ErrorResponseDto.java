package ru.sbrf.pprbts.oauth.config.exception.type;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Идентификатор запроса.
     */
    @Pattern(regexp = "^[a-z0-9]{16,32}$")
    @jakarta.validation.constraints.NotBlank
    private String traceId;

    /**
     * Сообщение об ошибке.
     */
    @Pattern(regexp = "^[\\w\\W]{1,1024}$")
    @jakarta.validation.constraints.NotBlank
    private String message;
}
