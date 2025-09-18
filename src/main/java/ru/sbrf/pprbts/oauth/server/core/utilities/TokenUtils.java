package ru.sbrf.pprbts.oauth.server.core.utilities;

import lombok.experimental.UtilityClass;
import ru.sbrf.pprbts.oauth.config.exception.type.TokenExchangeException;
import ru.sbrf.pprbts.oauth.server.business.model.OAuthTokensResponseDto;


@UtilityClass
public class TokenUtils {

    public static final String BASE_TOKEN_ERROR_MSG = "Failed to exchange tokens: %s";

    public static void validateExchangeTokenResponse(OAuthTokensResponseDto tokensResponse) {
        if (tokensResponse == null) {
            String errorMsg = BASE_TOKEN_ERROR_MSG.formatted("token response is null");
            throw new TokenExchangeException(errorMsg);
        }

        if (tokensResponse.getAccessToken() == null) {
            String errorMsg = BASE_TOKEN_ERROR_MSG.formatted("access token is null");
            throw new TokenExchangeException(errorMsg);
        } else if (tokensResponse.getRefreshToken() == null) {
            String errorMsg = BASE_TOKEN_ERROR_MSG.formatted("refresh token is null");
            throw new TokenExchangeException(errorMsg);
        }
    }
}
