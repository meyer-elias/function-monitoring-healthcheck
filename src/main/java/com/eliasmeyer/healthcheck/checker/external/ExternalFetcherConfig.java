package com.eliasmeyer.healthcheck.checker.external;

import javax.inject.Inject;

record ExternalFetcherConfig(String clientId, String clientSecret, String grantType, String tokenUrl, String urlExternalHealthCheck) {

    @Inject
    ExternalFetcherConfig {
    }
}
