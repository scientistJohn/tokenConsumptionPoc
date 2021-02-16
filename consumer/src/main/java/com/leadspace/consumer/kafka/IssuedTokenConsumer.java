package com.leadspace.consumer.kafka;

import com.leadspace.consumer.dto.TokenExpirationDto;
import com.leadspace.consumer.service.ProcessingService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.concurrent.ExecutorService;

public class IssuedTokenConsumer {
    private final Consumer<String, TokenExpirationDto> kafkaConsumer;
    private final ProcessingService processingService;
    private final ExecutorService executor;

    public IssuedTokenConsumer(Consumer<String, TokenExpirationDto> kafkaConsumer,
                               ProcessingService processingService,
                               ExecutorService executor) {
        this.kafkaConsumer = kafkaConsumer;
        this.processingService = processingService;
        this.executor = executor;
    }

    public void consume() {
        kafkaConsumer.poll(Duration.ofSeconds(1))
                .iterator()
                .forEachRemaining(this::consumeRecord);
    }

    private void consumeRecord(ConsumerRecord<String, TokenExpirationDto> record) {
        executor.submit(() -> processingService.consumeNewToken(record.value()));
    }


}
