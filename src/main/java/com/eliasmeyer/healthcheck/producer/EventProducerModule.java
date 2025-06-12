package com.eliasmeyer.healthcheck.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maps.pagamentos.dtos.monitoring.HealthDTO;
import com.eliasmeyer.healthcheck.json.JsonModule;
import dagger.Module;
import dagger.Provides;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

@Module(
    includes = {
        JsonModule.class
    })
public class EventProducerModule {

    @Provides
    @Singleton
    KafkaProducer<String, HealthDTO> kafkaProducer(KafkaProducerConfig kafkaProducerConfig, ObjectMapper objectMapper) {
        // Kafka producer properties
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerConfig.bootstrapServer());
        properties.put(ProducerConfig.RETRIES_CONFIG, kafkaProducerConfig.retries()); // Number of retries
        properties.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, kafkaProducerConfig.retryBackoffMs()); // Wait time between retries in milliseconds
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProducerConfig.requestTimeoutMs()); // Request timeout in milliseconds
        properties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG,
            kafkaProducerConfig.deliveryTimeoutMs()); // Total time to send a message (includes retries)
        properties.put(ProducerConfig.ACKS_CONFIG, "all"); // Wait for all replicas to acknowledge the message
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 512); // 1 KB
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 100); // wait up to 100ms to fill batch
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        //To connect AWS MSK in development
        properties.put("security.protocol", "SASL_SSL");
        properties.put("sasl.mechanism", "AWS_MSK_IAM");
        properties.put("sasl.jaas.config", "software.amazon.msk.auth.iam.IAMLoginModule required;");
        properties.put("sasl.client.callback.handler.class",
            "software.amazon.msk.auth.iam.IAMClientCallbackHandler");

        return new KafkaProducer<>(properties, new StringSerializer(),
            new HealthCheckSerializer(objectMapper));
    }

    @Provides
    @Singleton
    KafkaProducerConfig kafkaProducerConfig() {
        return new KafkaProducerConfig(System.getenv("KAFKA_BOOTSTRAP_SERVERS"),
            System.getenv("KAFKA_TOPIC_NAME"),
            Integer.parseInt(System.getenv("KAFKA_RETRIES")),
            Integer.parseInt(System.getenv("KAFKA_RETRY_BACKOFF_MS")),
            Integer.parseInt(System.getenv("KAFKA_REQUEST_TIMEOUT_MS")),
            Integer.parseInt(System.getenv("KAFKA_DELIVERY_TIMEOUT_MS"))
        );
    }
}
