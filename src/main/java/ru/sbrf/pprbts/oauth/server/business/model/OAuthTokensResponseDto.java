package ru.sbrf.pprbts.oauth.server.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthTokensResponseDto {

    private String accessToken;

    private String refreshToken;

    private String idToken;
}
