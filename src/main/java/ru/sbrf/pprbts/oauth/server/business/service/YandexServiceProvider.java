package ru.sbrf.pprbts.oauth.server.business.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.sbrf.pprbts.oauth.config.exception.type.TokenExchangeException;
import ru.sbrf.pprbts.oauth.config.properties.OAuthServiceProperties;
import ru.sbrf.pprbts.oauth.config.properties.YandexProviderProperties;
import ru.sbrf.pprbts.oauth.server.business.model.ErrorTokenExchangeResponseDto;
import ru.sbrf.pprbts.oauth.server.business.model.OAuthRefreshTokenRequestDto;
import ru.sbrf.pprbts.oauth.server.business.model.OAuthTokensResponseDto;
import ru.sbrf.pprbts.oauth.server.business.model.Scope;
import ru.sbrf.pprbts.oauth.server.core.utilities.TokenUtils;

import java.util.stream.Collectors;

import static ru.sbrf.pprbts.oauth.server.core.utilities.Constants.OAuth;


@Service
@Slf4j
@RequiredArgsConstructor
public class YandexServiceProvider extends OAuthServiceProvider {

    private final OAuthServiceProperties oAuthServiceProperties;

    private final RestClient restClient;

    private final ObjectMapper objectMapper;

    @Override
    protected String prepareAuthorizeUriImpl(@NonNull String state) {
        YandexProviderProperties yandexProviderProperties = oAuthServiceProperties.getProviders()
                .getYandex();
        String scopes = prepareScopes();

        return UriComponentsBuilder.fromUriString(yandexProviderProperties.getAuthorizationUri())
                .queryParam(OAuth.RequestParams.RESPONSE_TYPE, yandexProviderProperties.getResponseType())
                .queryParam(OAuth.RequestParams.CLIENT_ID, yandexProviderProperties.getClientId())
                .queryParam(OAuth.RequestParams.CLIENT_SECRET, yandexProviderProperties.getClientSecret())
                .queryParam(OAuth.RequestParams.SCOPE, scopes)
                .queryParam(OAuth.RequestParams.STATE, state)
                .build()
                .toUriString();
    }

    @Override
    protected OAuthTokensResponseDto exchangeTokensImpl(@NonNull String code) {
        return exchangeTokens(prepareTokensRequestBody(code));
    }

    @Override
    protected OAuthTokensResponseDto refreshTokensImpl(@NonNull OAuthRefreshTokenRequestDto refreshTokenDto) {
        return exchangeTokens(prepareTokensRefreshRequestBody(refreshTokenDto.getRefreshToken()));
    }

    private OAuthTokensResponseDto exchangeTokens(MultiValueMap<String, String> body) {
        YandexProviderProperties yandexProviderProperties = oAuthServiceProperties.getProviders()
                .getYandex();

        ResponseEntity<OAuthTokensResponseDto> responseEntity = restClient
                .post()
                .uri(yandexProviderProperties.getTokensUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, handleError())
                .toEntity(OAuthTokensResponseDto.class);

        TokenUtils.validateExchangeTokenResponse(responseEntity.getBody());

        return responseEntity.getBody();
    }

    private RestClient.ResponseSpec.ErrorHandler handleError() {
        return (ignore, response) -> {
            ErrorTokenExchangeResponseDto errorResponse = objectMapper.readValue(
                    response.getBody(), ErrorTokenExchangeResponseDto.class
            );

            String errorMsg = TokenUtils.BASE_TOKEN_ERROR_MSG.formatted(errorResponse.toString());
            throw new TokenExchangeException(errorMsg);
        };
    }

    private MultiValueMap<String, String> prepareTokensRequestBody(@NonNull String code) {
        YandexProviderProperties yandexProviderProperties = oAuthServiceProperties.getProviders()
                .getYandex();
        String scopes = prepareScopes();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add(OAuth.RequestParams.GRANT_TYPE, yandexProviderProperties.getGrantType());
        formData.add(OAuth.RequestParams.CODE, code);
        formData.add(OAuth.RequestParams.CLIENT_SECRET, yandexProviderProperties.getClientSecret());
        formData.add(OAuth.RequestParams.CLIENT_ID, yandexProviderProperties.getClientId());
        formData.add(OAuth.RequestParams.SCOPE, scopes);

        return formData;
    }

    private MultiValueMap<String, String> prepareTokensRefreshRequestBody(@NonNull String refreshToken) {
        YandexProviderProperties yandexProviderProperties = oAuthServiceProperties.getProviders()
                .getYandex();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add(OAuth.RequestParams.GRANT_TYPE, OAuth.GrantType.REFRESH_TOKEN);
        formData.add(OAuth.RequestParams.REFRESH_TOKEN, refreshToken);
        formData.add(OAuth.RequestParams.CLIENT_ID, yandexProviderProperties.getClientId());

        return formData;
    }

    private String prepareScopes() {
        YandexProviderProperties yandexProviderProperties = oAuthServiceProperties.getProviders()
                .getYandex();
        return yandexProviderProperties.getScopes()
                .stream()
                .map(Scope::getValue)
                .collect(Collectors.joining("%20"));
    }
}
