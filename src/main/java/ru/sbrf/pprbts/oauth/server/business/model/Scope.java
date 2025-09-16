package ru.sbrf.pprbts.oauth.server.business.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum Scope {

    OPENID("openid"),

    BASIC("basic"),

    EXTENDED("extended"),

    ACCESS("access"),

    CERT("cert"),

    SAP("sap"),

    NAME("name"),

    // Да-да, опечатка. Но все согласно доке!
    PREFFERED_USERNAME("preffered_username"),

    LEGACY("legacy"),

    EMPLOYEE_NUMBER("employee_number"),

    HRP("hrp");

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
