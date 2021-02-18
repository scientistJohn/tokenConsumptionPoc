package com.leadspace.issuer.kafka;

import com.leadspace.issuer.service.UsageService;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class UsageConsumer {
    private final KafkaConsumer<String, String> consumer;
    private final UsageService usageService;
    private final AtomicBoolean normalMode = new AtomicBoolean(true);
    private final ExecutorService executorService;
    private final Map<TopicPartition, Long> offsetsBeforeReset = new HashMap<>();

    public UsageConsumer(KafkaConsumer<String, String> consumer,
                         UsageService usageService,
                         ExecutorService executorService) {
        this.consumer = consumer;
        this.usageService = usageService;
        this.executorService = executorService;
    }

    public void consume() {
        if (normalMode.get()) {
            consumer.poll(Duration.ofSeconds(1))
                    .forEach(r -> executorService.submit(() -> usageService.handleUsage(r.value(), r.timestamp() / 1000L)));
        } else {
            Map<String, List<Long>> usage = new HashMap<>();
            consumer.poll(Duration.ofSeconds(1))
                    .forEach(r -> {
                        TopicPartition topicPartition = new TopicPartition(r.topic(), r.partition());
                        if (offsetsBeforeReset.containsKey(topicPartition)
                                && offsetsBeforeReset.get(topicPartition) <= r.offset()) {
                            offsetsBeforeReset.remove(topicPartition);
                        }

                        usage.computeIfAbsent(r.value(), k -> new ArrayList<>()).add(r.timestamp() / 1000L);
                    });
            executorService.submit(() -> usageService.handleUsage(usage));
            if (offsetsBeforeReset.isEmpty()) {
                normalMode.set(true);
            }
        }
    }

    public void resetConsumer(long timestamp) {
        normalMode.set(false);
        Map<TopicPartition, Long> offsetsForTimes = new HashMap<>();
        consumer.assignment().forEach(topicPartition -> {
            offsetsBeforeReset.put(topicPartition, consumer.position(topicPartition));
            offsetsForTimes.put(topicPartition, timestamp);
        });
        Map<TopicPartition, OffsetAndTimestamp> offsets = consumer.offsetsForTimes(offsetsForTimes);
        if (offsets != null) {
            offsets.forEach((k, v) -> consumer.seek(k, v.offset()));
        }
    }
}
