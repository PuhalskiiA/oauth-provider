package ru.sbrf.pprbts.oauth.server.business.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sbrf.pprbts.oauth.config.exception.type.ServiceException;
import ru.sbrf.pprbts.oauth.server.business.model.OAuthRefreshTokenRequestDto;
import ru.sbrf.pprbts.oauth.server.business.model.OAuthTokensResponseDto;
import ru.sbrf.pprbts.oauth.server.business.service.OAuthServiceProvider;
import ru.sbrf.pprbts.oauth.server.core.utilities.OAuthUtils;

import java.io.IOException;


@Controller
@RequiredArgsConstructor
@RequestMapping("${oauth-service.base-path:/api/v1/oauth}")
@Slf4j
public class OAuthServiceController {

    private static final String SUCCESS_PAGE_NAME = "success_oauth_page";

    private final OAuthServiceProvider sudirServiceProvider;

    @GetMapping(value = "/login")
    @SneakyThrows(ServiceException.class)
    public void login(
            @RequestParam("deep_link") String deepLink,
            HttpServletResponse response
    ) {
        String authorizeUri = sudirServiceProvider.prepareAuthorizeUri(deepLink);
        try {
            response.sendRedirect(authorizeUri);
        } catch (IOException e) {
            log.error("Something went wrong during the redirect to the authentication service", e);
            throw new ServiceException("Something went wrong during the redirect to the authentication service");
        }
    }

    @GetMapping(value = "/openid-connect-auth/redirect_uri")
    public String receiveCode(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            Model model
    ) {
        String redirectUrl = OAuthUtils.prepareRedirectUriFromState(code, state);
        model.addAttribute("redirectUrl", redirectUrl);
        return SUCCESS_PAGE_NAME;
    }

    @GetMapping(
            value = "/receive/token",
            produces = {"application/json"}
    )
    public ResponseEntity<OAuthTokensResponseDto> receiveTokens(
            @RequestParam("code") String code,
            @RequestParam("state") String state
    ) {
        OAuthTokensResponseDto tokenResponse = sudirServiceProvider.exchangeTokens(code, state);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping(
            value = "/refresh",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<OAuthTokensResponseDto> refresh(@RequestBody OAuthRefreshTokenRequestDto currentTokensDto) {
        OAuthTokensResponseDto tokenResponse = sudirServiceProvider.refreshTokens(currentTokensDto);
        return ResponseEntity.ok(tokenResponse);
    }
}
