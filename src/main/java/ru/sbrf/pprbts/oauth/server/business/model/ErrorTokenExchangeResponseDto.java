package ru.sbrf.pprbts.oauth.server.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorTokenExchangeResponseDto {

    private String errorDescription;

    private String error;

    @Override
    public String toString() {
        return "error: %s, description: %s".formatted(error, errorDescription);
    }
}