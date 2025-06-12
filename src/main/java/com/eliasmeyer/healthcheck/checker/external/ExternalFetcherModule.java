package com.eliasmeyer.healthcheck.checker.external;

import com.maps.pagamentos.dtos.common.responses.HealthCheckIntegrationResponse;
import com.eliasmeyer.healthcheck.checker.external.http.HttpCustomModule;
import com.eliasmeyer.healthcheck.checker.external.http.HttpService;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module(
    includes = {
        HttpCustomModule.class
    })
public class ExternalFetcherModule {

    @Provides
    @Singleton
    ExternalFetcherConfig externalFetcherConfig() {
        return new ExternalFetcherConfig(
            System.getenv("OAUTH_CLIENT_ID"),
            System.getenv("OAUTH_CLIENT_SECRET"),
            System.getenv("OAUTH_GRANT_TYPE"),
            System.getenv("OAUTH_TOKEN_URL"),
            System.getenv("URL_CORE_BANKING_HEALTH_CHECK"));
    }

    @Provides
    @Singleton
    @Named("tokenFetcher")
    ExternalFetcher<Void, Token> tokenFetcher(HttpService httpService, ExternalFetcherConfig externalFetcherConfig) {
        return new TokenFetcher(httpService, externalFetcherConfig);
    }

    @Provides
    @Singleton
    @Named("coreBankingFetcher")
    ExternalFetcher<Token, HealthCheckIntegrationResponse> coreBankingFetcher(HttpService httpService,
        ExternalFetcherConfig externalFetcherConfig) {
        return new CoreBankingFetcher(httpService, externalFetcherConfig);
    }
}
