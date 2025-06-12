package com.eliasmeyer.healthcheck.producer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.maps.pagamentos.dtos.monitoring.HealthDTO;
import com.maps.pagamentos.dtos.monitoring.StatusHealth;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class HealthSerializerTest {

    @Test
    void serialize_shouldReturnValidJsonBytes() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        HealthCheckSerializer serializer = new HealthCheckSerializer(objectMapper);

        LocalDateTime dateTimeExpected = LocalDateTime.of(2025, 1, 30, 12, 5, 31);

        HealthDTO dto = new HealthDTO(StatusHealth.UP, dateTimeExpected);

        byte[] bytes = serializer.serialize("topic", dto);

        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        // Verify round-trip
        HealthDTO result = objectMapper.readValue(bytes, HealthDTO.class);
        assertEquals(StatusHealth.UP, result.getStatus());
        assertEquals(dateTimeExpected, result.getTimestamp());
    }
}