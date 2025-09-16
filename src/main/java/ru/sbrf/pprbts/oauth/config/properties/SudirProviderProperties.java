package ru.sbrf.pprbts.oauth.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.sbrf.pprbts.oauth.server.business.model.Scope;

import java.util.Set;


@Getter
@Setter
@ToString
public class SudirProviderProperties {

    /**
     * URI для страницы авторизации.
     */
    @NotBlank(message = "Authorization URI не должно быть пустым")
    private String authorizationUri;

    /**
     * URI для получения токена доступа.
     */
    @NotBlank(message = "Token URI не должно быть пустым")
    private String tokensUri;

    /**
     * URI для перенаправления кода аутентификации.
     */
    @NotBlank(message = "Redirect URI не должно быть пустым")
    private String redirectUri;

    /**
     * Идентификатор клиента (Client ID) приложения.
     */
    @NotBlank(message = "Client ID не должен быть пустым")
    private String clientId;

    /**
     * Тип гранта (grant type), например: password, client_credentials.
     */
    @NotBlank(message = "Grant Type не должен быть пустым")
    private String grantType;

    /**
     * Тип ответа в ручке авторизации.
     */
    @NotBlank(message = "Response Type не должен быть пустым")
    private String responseType;

    /**
     * Набор областей (scopes), необходимых для доступа.
     */
    @NotNull(message = "Scopes должны быть заданы")
    private Set<Scope> scopes;
}
