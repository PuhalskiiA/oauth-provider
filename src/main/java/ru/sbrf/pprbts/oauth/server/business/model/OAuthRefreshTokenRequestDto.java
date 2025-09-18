package ru.sbrf.pprbts.oauth.server.business.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class OAuthRefreshTokenRequestDto {

    @NotBlank
    private String refreshToken;
}
