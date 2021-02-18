package com.leadspace.issuer.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ValidTokenProducer {
    private final KafkaProducer<String, Integer> producer;
    private final String topic;

    public ValidTokenProducer(KafkaProducer<String, Integer> producer,
                              String topic) {
        this.producer = producer;
        this.topic = topic;
    }

    public void sendValid(String token) {
        try {
            ProducerRecord<String, Integer> record = new ProducerRecord<>(topic, token, 1);
            producer.send(record, (metadata, exception) -> System.out.println(metadata)).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public void sendInvalid(String token) {
        try {
            ProducerRecord<String, Integer> record = new ProducerRecord<>(topic, token, 0);
            producer.send(record, (metadata, exception) -> System.out.println(metadata)).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
