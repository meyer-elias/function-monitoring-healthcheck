package com.eliasmeyer.healthcheck.checker.external;

import java.util.Objects;

public record Token(String access_token) {

    public Token {
        if (Objects.isNull(access_token) || access_token.isBlank()) {
            throw new IllegalArgumentException("Access token cannot be null or blank");
        }
    }
}
