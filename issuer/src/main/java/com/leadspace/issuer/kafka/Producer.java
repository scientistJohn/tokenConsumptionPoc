package com.leadspace.issuer.kafka;

import com.leadspace.issuer.dto.TokenExpirationDto;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class Producer {
    private final KafkaProducer<String, TokenExpirationDto> producer;
    private final String topic;

    public Producer(KafkaProducer<String, TokenExpirationDto> producer,
                    String topic) {
        this.producer = producer;
        this.topic = topic;
    }

    public void send(String token, TokenExpirationDto expirationDto) {
        ProducerRecord<String, TokenExpirationDto> record = new ProducerRecord<>(topic, token, expirationDto);
        producer.send(record, (metadata, exception) -> System.out.println(metadata));
    }
}
