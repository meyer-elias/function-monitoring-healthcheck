package com.eliasmeyer.healthcheck.monitoring;

import dagger.Component;
import javax.inject.Singleton;

@Component(
    modules = {
        HealthCheckMonitoringModule.class,
    })
@Singleton
public interface HealthCheckMonitoringFactory {

    HealthCheckMonitoring monitoring();
}
