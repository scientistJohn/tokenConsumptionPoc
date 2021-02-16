package com.leadspace.consumer.kafka;

import com.leadspace.consumer.service.ProcessingService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.concurrent.ExecutorService;

public class WorkRequestConsumer {
    private final Consumer<String, String> kafkaConsumer;
    private final ProcessingService processingService;
    private final ExecutorService executor;

    public WorkRequestConsumer(Consumer<String, String> kafkaConsumer,
                               ProcessingService processingService,
                               ExecutorService executor) {
        this.kafkaConsumer = kafkaConsumer;
        this.processingService = processingService;
        this.executor = executor;
    }

    public void consume() {
        kafkaConsumer.poll(Duration.ofMillis(100))
                .iterator()
                .forEachRemaining(this::consumeRecord);
    }

    private void consumeRecord(ConsumerRecord<String, String> record) {
        System.out.println("Consumed workRequest: " + record.value());
        executor.submit(() -> processingService.process(record.value()));
    }
}
