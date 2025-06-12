package com.eliasmeyer.healthcheck.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maps.pagamentos.dtos.monitoring.HealthDTO;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class HealthCheckSerializer implements Serializer<HealthDTO> {

    private final ObjectMapper objectMapper;

    private static final Logger logger = LogManager.getLogger(HealthCheckSerializer.class);

    public HealthCheckSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(String s, HealthDTO healthDTO) {
        byte[] output;
        try {
            output = objectMapper.writeValueAsBytes(healthDTO);
        } catch (Exception e) {
            logger.error("Error serializing health check to JSON: {}", e.getMessage());
            output = new byte[0];
        }
        return output;
    }
}
