package com.eliasmeyer.healthcheck.checker.external.http;

import javax.inject.Inject;

record HttpConfig(long connectionTimeout, long responseTimeout, int retries) {

    @Inject
    public HttpConfig {
    }
}
