package ru.sbrf.pprbts.oauth.server.business.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum Scope {

    EMAIL("login:email"),

    INFO("login:info");

    private final String value;

    public static Scope fromValue(String value) {
        for (Scope scope : values()) {
            if (scope.value.equals(value)) {
                return scope;
            }
        }
        throw new IllegalArgumentException("Unknown scope: %s".formatted(value));
    }
}
