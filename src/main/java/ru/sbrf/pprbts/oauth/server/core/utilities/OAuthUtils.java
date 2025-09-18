package ru.sbrf.pprbts.oauth.server.core.utilities;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;


@UtilityClass
public class OAuthUtils {

    private static final String BASE_REDIRECT_URI_TEMPLATE = "%s?code=%s&state=%s";

    public static String prepareRedirectUriFromState(String code, String state) {
        if (StringUtils.isBlank(state)) {
            throw new IllegalArgumentException("State cannot be null or empty");
        }

        String[] parts = state.split(" ");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid state format. Expected: redirectUri.id");
        }

        String redirectUri = parts[0];
        String operationId = parts[1];

        return BASE_REDIRECT_URI_TEMPLATE.formatted(redirectUri, code, operationId);
    }

    public static String prepareState(String deepLink, UUID operationId) {
        return "%s%%20%s".formatted(deepLink, operationId);
    }
}
