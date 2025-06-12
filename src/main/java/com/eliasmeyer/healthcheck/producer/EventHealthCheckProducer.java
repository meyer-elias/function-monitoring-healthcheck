package com.eliasmeyer.healthcheck.producer;

import com.maps.pagamentos.dtos.monitoring.HealthDTO;
import jakarta.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import javax.inject.Singleton;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
public class EventHealthCheckProducer {

    private final KafkaProducer<String, HealthDTO> kafkaProducer;

    private final KafkaProducerConfig kafkaProducerConfig;

    private static final Logger logger = LogManager.getLogger(EventHealthCheckProducer.class);

    private static final String HEADER_TYPE_ID = "__TypeId__";

    @Inject
    public EventHealthCheckProducer(KafkaProducer<String, HealthDTO> kafkaProducer, KafkaProducerConfig kafkaProducerConfig) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaProducerConfig = kafkaProducerConfig;
    }

    public void send(HealthDTO health) {
        final String uuid = UUID.randomUUID().toString();
        ProducerRecord<String, HealthDTO> message = new ProducerRecord<>(kafkaProducerConfig.topicName(), UUID.randomUUID().toString(), health);
        // Need add custom header (e.g., type info)
        message.headers().add(new RecordHeader(HEADER_TYPE_ID, HealthDTO.class.getName().getBytes(StandardCharsets.UTF_8)));

        kafkaProducer.send(message, (metadata, exception) -> {
            if (Objects.nonNull(exception)) {
                logger.error("Error sending health check [{}] to Kafka: {}", uuid, exception.getMessage());
            } else {
                logger.info("Health check sent [{}] to Kafka successfully: {}", uuid, metadata);
            }
        });
        kafkaProducer.flush();
    }
}
