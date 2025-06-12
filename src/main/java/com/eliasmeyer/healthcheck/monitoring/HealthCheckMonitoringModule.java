package com.eliasmeyer.healthcheck.monitoring;

import com.eliasmeyer.healthcheck.checker.HealthCheckerModule;
import com.eliasmeyer.healthcheck.producer.EventProducerModule;
import dagger.Binds;
import dagger.Module;
import javax.inject.Singleton;

@Module(
    includes = {
        HealthCheckerModule.class,
        EventProducerModule.class
    })
abstract class HealthCheckMonitoringModule {

    @Binds
    @Singleton
    abstract HealthCheckMonitoring healthCheckMonitoring(HealthCheckMonitoringResource impl);
}
