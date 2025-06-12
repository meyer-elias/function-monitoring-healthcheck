package com.eliasmeyer.healthcheck.monitoring;

import com.maps.pagamentos.dtos.monitoring.HealthDTO;
import com.maps.pagamentos.dtos.monitoring.StatusHealth;
import com.eliasmeyer.healthcheck.checker.HealthChecker;
import com.eliasmeyer.healthcheck.exception.HealthCheckMonitoringException;
import com.eliasmeyer.healthcheck.producer.EventHealthCheckProducer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
class HealthCheckMonitoringResource implements HealthCheckMonitoring {

    private final HealthChecker healthChecker;

    private final EventHealthCheckProducer eventHealthCheckProducer;

    private static final Logger logger = LogManager.getLogger(HealthCheckMonitoringResource.class);

    @Inject
    public HealthCheckMonitoringResource(HealthChecker healthChecker, EventHealthCheckProducer eventHealthCheckProducer) {
        this.healthChecker = healthChecker;
        this.eventHealthCheckProducer = eventHealthCheckProducer;
    }

    public boolean isOk() {
        HealthDTO health;
        try {
            health = healthChecker.isAlive() ? up() : down();
        } catch (HealthCheckMonitoringException e) {
            logger.error("Error checking health status", e);
            health = down();
        }

        // Send health status to Kafka
        eventHealthCheckProducer.send(health);
        return health.getStatus().isUp();
    }

    private static HealthDTO up() {
        return new HealthDTO(StatusHealth.UP, LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
    }

    private static HealthDTO down() {
        return new HealthDTO(StatusHealth.DOWN, LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
    }
}
