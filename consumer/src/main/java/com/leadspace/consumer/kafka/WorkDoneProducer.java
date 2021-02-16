package com.leadspace.consumer.kafka;

import com.leadspace.consumer.dto.WorkDoneDto;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class WorkDoneProducer {
    private final Producer<String, WorkDoneDto> producer;
    private final String topic;
    private final String clientId;

    public WorkDoneProducer(Producer<String, WorkDoneDto> producer, String topic, String clientId) {
        this.producer = producer;
        this.topic = topic;
        this.clientId = clientId;
    }

    public void report(String token, boolean status) {
        producer.send(new ProducerRecord<>(topic, token, new WorkDoneDto(clientId, status)));
    }
}
