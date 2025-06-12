package com.eliasmeyer.healthcheck.monitoring;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.maps.pagamentos.dtos.monitoring.StatusHealth;
import com.eliasmeyer.healthcheck.checker.HealthChecker;
import com.eliasmeyer.healthcheck.exception.HealthCheckMonitoringException;
import com.eliasmeyer.healthcheck.producer.EventHealthCheckProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HealthCheckMonitoringResourceTest {

    private HealthChecker healthChecker;

    private EventHealthCheckProducer eventHealthCheckProducer;

    private HealthCheckMonitoringResource resource;

    @BeforeEach
    void setUp() {
        healthChecker = mock(HealthChecker.class);
        eventHealthCheckProducer = mock(EventHealthCheckProducer.class);
        resource = new HealthCheckMonitoringResource(healthChecker, eventHealthCheckProducer);
    }

    @Test
    void isOk_shouldReturnTrue_whenHealthCheckerIsAlive() {
        when(healthChecker.isAlive()).thenReturn(true);

        boolean result = resource.isOk();

        assertTrue(result);
        verify(eventHealthCheckProducer).send(argThat(h -> h.getStatus() == StatusHealth.UP));
    }

    @Test
    void isOk_shouldReturnFalse_whenHealthCheckerIsNotAlive() {
        when(healthChecker.isAlive()).thenReturn(false);

        boolean result = resource.isOk();

        assertFalse(result);
        verify(eventHealthCheckProducer).send(argThat(h -> h.getStatus() == StatusHealth.DOWN));
    }

    @Test
    void isOk_shouldReturnFalse_andLogError_whenExceptionThrown() {
        when(healthChecker.isAlive()).thenThrow(new HealthCheckMonitoringException("error"));

        boolean result = resource.isOk();

        assertFalse(result);
        verify(eventHealthCheckProducer).send(argThat(h -> h.getStatus() == StatusHealth.DOWN));
    }
}