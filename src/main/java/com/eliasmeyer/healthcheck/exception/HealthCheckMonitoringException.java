package com.eliasmeyer.healthcheck.exception;

public class HealthCheckMonitoringException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public HealthCheckMonitoringException(String message, Throwable cause) {
        super(message, cause);
    }

    public HealthCheckMonitoringException(String message) {
        super(message);
    }
}
