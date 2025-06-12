package com.eliasmeyer.healthcheck.checker.external;

import com.eliasmeyer.healthcheck.checker.external.http.HttpService;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named("tokenFetcher")
class TokenFetcher implements ExternalFetcher<Void, Token> {

    private final HttpService httpService;

    private final ExternalFetcherConfig externalFetcherConfig;

    @Inject
    public TokenFetcher(HttpService httpService, ExternalFetcherConfig externalFetcherConfig) {
        this.httpService = httpService;
        this.externalFetcherConfig = externalFetcherConfig;
    }

    @Override
    public Token fetch(Void request) {
        var body = Map.of(
            "client_id", externalFetcherConfig.clientId(),
            "client_secret", externalFetcherConfig.clientSecret(),
            "grant_type", externalFetcherConfig.grantType()
        );

        var header = Map.of(
            "Content-Type", "application/x-www-form-urlencoded",
            "Accept", "application/json"
        );

        // Create the HTTP client
        return httpService.form(externalFetcherConfig.tokenUrl(), body, Token.class, header);
    }
}
