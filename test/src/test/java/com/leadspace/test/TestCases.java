package com.leadspace.test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestCases {

    private KafkaProducer<String, CreateTokenRequest> tokenRequestProducer;
    private KafkaProducer<String, String> workRequestProducer;
    private Consumer<String, WorkDoneDto> workDoneConsumer;

    @BeforeAll
    public void init() {
        Properties workRequestProducerProps = new Properties();
        workRequestProducerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092,localhost:29092,localhost:39092");
        workRequestProducerProps.put(ProducerConfig.CLIENT_ID_CONFIG, "test");
        workRequestProducerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        workRequestProducerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        workRequestProducer = new KafkaProducer<>(workRequestProducerProps);

        Properties workDoneConsumerProps = new Properties();
        workDoneConsumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092,localhost:29092,localhost:39092");
        workDoneConsumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        workDoneConsumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        workDoneConsumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, WorkDoneDtoDeserializer.class.getName());
        workDoneConsumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        workDoneConsumer = new KafkaConsumer<>(workDoneConsumerProps);
        workDoneConsumer.subscribe(Collections.singleton("work-done-topic"));

        Properties tokenRequestProducerProps = new Properties();
        tokenRequestProducerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092,localhost:29092,localhost:39092");
        tokenRequestProducerProps.put(ProducerConfig.CLIENT_ID_CONFIG, "test");
        tokenRequestProducerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        tokenRequestProducerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CreateTokenRequestSerializer.class.getName());
        tokenRequestProducer = new KafkaProducer<>(tokenRequestProducerProps);
    }

    @AfterAll
    public void cleanUp() {
        workDoneConsumer.close();
        workRequestProducer.close();
    }

    @Test
    public void treeConsumerWorkInParallel() throws Exception {
        ZonedDateTime from = ZonedDateTime.now();
        ZonedDateTime to = ZonedDateTime.now().plusDays(1L);
        final long[] usage = {120};
        String token = UUID.randomUUID().toString();
        System.out.println(token);
        CreateTokenRequest request = new CreateTokenRequest(token, Period.HOURLY, 100L);

        tokenRequestProducer.send(new ProducerRecord<>("token-request-topic", "POST", request));

        Thread.sleep(5000L);
        for (int i = 0; i < usage[0]; i++) {
            workRequestProducer.send(new ProducerRecord<>("work-request-topic", i%3, token, Integer.valueOf(i).toString()));
        }
        Map<String, Integer> consumersUsage = new HashMap<>();
        while (usage[0] > 0) {
            workDoneConsumer.poll(Duration.ofSeconds(2L))
                    .iterator()
                    .forEachRemaining(r -> {
                        WorkDoneDto done = r.value();
                        if (!r.key().equals(token)) {
                            return;
                        }
                        //assertTrue(done.isResult());
                        int count = consumersUsage.getOrDefault(done.getClient(), 0);
                        consumersUsage.put(done.getClient(), ++count);
                        usage[0]--;
                    });
        }

        assertEquals(3, consumersUsage.size());
    }


}
