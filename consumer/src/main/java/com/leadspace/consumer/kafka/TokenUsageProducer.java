package com.leadspace.consumer.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class TokenUsageProducer {
    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final String clientId;

    public TokenUsageProducer(KafkaProducer<String, String> producer,
                              String topic,
                              String clientId) {
        this.producer = producer;
        this.topic = topic;
        this.clientId = clientId;
    }

    public void produce(String token) {
        producer.send(new ProducerRecord<>(topic, token, clientId));
    }
}
