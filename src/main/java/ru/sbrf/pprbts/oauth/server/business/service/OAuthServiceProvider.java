package ru.sbrf.pprbts.oauth.server.business.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.sbrf.pprbts.oauth.server.business.model.OAuthRefreshTokenRequestDto;
import ru.sbrf.pprbts.oauth.server.business.model.OAuthTokensResponseDto;
import ru.sbrf.pprbts.oauth.server.core.utilities.OAuthUtils;

import java.util.UUID;
import java.util.function.Supplier;


@Slf4j
public abstract class OAuthServiceProvider {

    protected abstract String prepareAuthorizeUriImpl(@NonNull String state);

    protected abstract OAuthTokensResponseDto exchangeTokensImpl(@NonNull String code);

    protected abstract OAuthTokensResponseDto refreshTokensImpl(@NonNull OAuthRefreshTokenRequestDto refreshToken);

    public String prepareAuthorizeUri(String deeplink) {
        UUID operationId = UUID.randomUUID();
        log.debug("Getting authorize uri for operation id: {} with provider {}", operationId, getProviderName());

        String state = OAuthUtils.prepareState(deeplink, operationId);

        return prepareAuthorizeUriImpl(state);
    }

    public OAuthTokensResponseDto exchangeTokens(@NonNull String code, @NonNull String state) {
        log.info("Successfully getting code for operation with id: {}", state);
        log.debug("Exchanging tokens for operation id: {} with provider {}", state, getProviderName());

        return receiveTokens(() -> exchangeTokensImpl(code), state);
    }

    public OAuthTokensResponseDto refreshTokens(@NonNull OAuthRefreshTokenRequestDto refreshToken) {
        UUID operationId = UUID.randomUUID();
        log.debug("Refreshing tokens for operation id: {} with provider {}", operationId, getProviderName());

        return receiveTokens(() -> refreshTokensImpl(refreshToken), operationId.toString());
    }

    private String getProviderName() {
        return this.getClass().getSimpleName();
    }

    private OAuthTokensResponseDto receiveTokens(Supplier<OAuthTokensResponseDto> supplier, String operationId) {
        OAuthTokensResponseDto response = supplier.get();
        log.info("Tokens for operation id: {} was received", operationId);

        return response;
    }
}
