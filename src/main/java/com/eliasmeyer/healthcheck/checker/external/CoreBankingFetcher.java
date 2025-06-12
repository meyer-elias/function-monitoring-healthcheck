package com.eliasmeyer.healthcheck.checker.external;

import com.maps.pagamentos.dtos.common.responses.HealthCheckIntegrationResponse;
import com.eliasmeyer.healthcheck.checker.external.http.HttpService;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named("coreBankingFetcher")
class CoreBankingFetcher implements ExternalFetcher<Token, HealthCheckIntegrationResponse> {

    private final HttpService httpService;

    private final ExternalFetcherConfig externalFetcherConfig;

    @Inject
    public CoreBankingFetcher(HttpService httpService, ExternalFetcherConfig externalFetcherConfig) {
        this.httpService = httpService;
        this.externalFetcherConfig = externalFetcherConfig;
    }

    @Override
    public HealthCheckIntegrationResponse fetch(Token token) {
        Map<String, String> headers = Map.of(
            "Authorization", "Bearer " + token.access_token(),
            "Content-Type", "application/json",
            "Accept", "application/json"
        );
        return httpService.get(
            externalFetcherConfig.urlExternalHealthCheck(),
            HealthCheckIntegrationResponse.class,
            headers
        );
    }
}
