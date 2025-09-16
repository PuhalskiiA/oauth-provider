package ru.sbrf.pprbts.oauth.config.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class OAuthProvidersProperties {

    @NotNull(message = "Обязательно должны быть указаны параметры для работы с СУДИРом!")
    private SudirProviderProperties sudir;
}
