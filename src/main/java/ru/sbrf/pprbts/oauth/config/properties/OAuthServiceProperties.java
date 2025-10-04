package ru.sbrf.pprbts.oauth.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;


@Component
@ConfigurationProperties(prefix = "oauth-service", ignoreUnknownFields = false)
@Validated
@Getter
@Setter
@NoArgsConstructor
public class OAuthServiceProperties {

    /**
     * Базовый url приложения.
     */
    @NotBlank(message = "Обязательно должен быть указан базовый url-path для эндпоинтов приложения!")
    @Valid
    private String basePath;

    /**
     * Данные для работы с СУДИРом.
     */
    @NotNull(message = "Обязательно должны быть указаны параметры провайдеров!")
    @Valid
    private OAuthProvidersProperties providers;
}
