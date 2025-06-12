package com.eliasmeyer.healthcheck.producer;

import javax.inject.Inject;

record KafkaProducerConfig(String bootstrapServer,
                           String topicName,
                           int retries,
                           int retryBackoffMs,
                           int requestTimeoutMs,
                           int deliveryTimeoutMs) {

    @Inject
    KafkaProducerConfig {
    }
}
