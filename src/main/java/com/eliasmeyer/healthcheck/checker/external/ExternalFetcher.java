package com.eliasmeyer.healthcheck.checker.external;

public interface ExternalFetcher<T, R> {

    R fetch(T request);

}
