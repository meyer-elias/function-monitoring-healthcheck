package com.eliasmeyer.healthcheck.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.eliasmeyer.healthcheck.monitoring.DaggerHealthCheckMonitoringFactory;
import com.eliasmeyer.healthcheck.monitoring.HealthCheckMonitoring;
import java.util.Map;

public class HealthCheckMonitoringHandler implements RequestHandler<Map<String, Object>, String> {

    @Override
    public String handleRequest(Map<String, Object> request, Context context) {
        // Log the request for debugging purposes
        LambdaLogger logger = context.getLogger();
        logger.log("Received request: " + context.getAwsRequestId());
        // Create an instance of HealthCheckMonitoring using Dagger
        HealthCheckMonitoring monitoring = DaggerHealthCheckMonitoringFactory.create().monitoring();
        if (monitoring.isOk()) {
            logger.log("Service is UP");
            return "Service is UP";
        } else {
            logger.log("Service is DOWN");
            return "Service is DOWN";
        }
    }
}
