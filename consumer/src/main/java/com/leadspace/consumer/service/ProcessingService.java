package com.leadspace.consumer.service;

import com.leadspace.consumer.dto.TokenExpirationDto;
import com.leadspace.consumer.kafka.TokenUsageProducer;
import com.leadspace.consumer.kafka.UsageStore;
import com.leadspace.consumer.kafka.WorkDoneProducer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class ProcessingService {
    private final Map<String, TokenExpirationDto> tokens = new HashMap<>();

    private final UsageStore usageStore;
    private final TokenUsageProducer tokenUsageProducer;
    private final WorkDoneProducer workDoneProducer;
    private final Worker worker;

    public ProcessingService(UsageStore usageStore, TokenUsageProducer tokenUsageProducer, WorkDoneProducer workDoneProducer, Worker worker) {
        this.usageStore = usageStore;
        this.tokenUsageProducer = tokenUsageProducer;
        this.workDoneProducer = workDoneProducer;
        this.worker = worker;
    }

    public void process(String token) {
        try {
            System.out.println("Process token:" + token);
            TokenExpirationDto expirationDto = tokens.get(token);
//        long allowedUsage = usageStore.getUsage(token,
//                Instant.ofEpochSecond(expirationDto.getFrom()),
//                Instant.ofEpochSecond(expirationDto.getTo()));
            long allowedUsage = usageStore.getUsage(token,
                    Instant.now().minus(1L, ChronoUnit.DAYS),
                    Instant.now().plus(1L, ChronoUnit.DAYS));
            System.out.println("Usage for token:" + token + " is: " + allowedUsage);
            if (allowedUsage < expirationDto.getUsage()) {
                tokenUsageProducer.produce(token);
                worker.work();
                workDoneProducer.report(token, true);
            } else {
                workDoneProducer.report(token, false);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void consumeNewToken(TokenExpirationDto expirationDto) {
        tokens.put(expirationDto.getToken(), expirationDto);
    }
}
