package com.eliasmeyer.healthcheck.checker;

import com.maps.pagamentos.dtos.common.responses.HealthCheckIntegrationResponse;
import com.eliasmeyer.healthcheck.checker.external.ExternalFetcher;
import com.eliasmeyer.healthcheck.checker.external.ExternalFetcherModule;
import com.eliasmeyer.healthcheck.checker.external.Token;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module(
    includes = {
        ExternalFetcherModule.class,
    })
public class HealthCheckerModule {

    @Provides
    @Singleton
    HealthChecker provideHealthChecker(@Named("tokenFetcher") ExternalFetcher<Void, Token> tokenFetcher,
        @Named("coreBankingFetcher") ExternalFetcher<Token, HealthCheckIntegrationResponse> coreBankingFetcher) {

        return new HealthCheckerIntegrationCoreBanking(tokenFetcher, coreBankingFetcher);
    }
}
