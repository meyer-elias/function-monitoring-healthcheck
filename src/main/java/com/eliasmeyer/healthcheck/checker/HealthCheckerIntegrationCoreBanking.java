package com.eliasmeyer.healthcheck.checker;

import com.maps.pagamentos.dtos.common.responses.HealthCheckIntegrationResponse;
import com.eliasmeyer.healthcheck.checker.external.ExternalFetcher;
import com.eliasmeyer.healthcheck.checker.external.Token;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.hc.core5.http.HttpStatus;

class HealthCheckerIntegrationCoreBanking implements HealthChecker {

    private final ExternalFetcher<Void, Token> tokenFetcher;

    private final ExternalFetcher<Token, HealthCheckIntegrationResponse> coreBankingfetcher;

    @Inject
    public HealthCheckerIntegrationCoreBanking(@Named("tokenFetcher") ExternalFetcher<Void, Token> tokenFetcher,
        @Named("coreBankingFetcher") ExternalFetcher<Token, HealthCheckIntegrationResponse> coreBankingfetcher) {
        this.tokenFetcher = tokenFetcher;
        this.coreBankingfetcher = coreBankingfetcher;
    }

    public boolean isAlive() {
        Token token = Optional.ofNullable(tokenFetcher.fetch(null))
            .orElseThrow(() -> new IllegalStateException("Failed to fetch token"));
        return isAlive(coreBankingfetcher.fetch(token));
    }

    private Boolean isAlive(HealthCheckIntegrationResponse response) {
        return response.getIntegrations()
            .stream()
            .allMatch(i -> i.getHttpStatus() == HttpStatus.SC_OK);
    }
}
