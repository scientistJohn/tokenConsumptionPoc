package com.leadspace.test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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

    private ResponseHandler<String> tokenResponseHandler = new TokenResponseHandler();
    private ObjectMapper objectMapper;
    private CloseableHttpClient httpClient;
    private KafkaProducer<String, String> workRequestProducer;
    private Consumer<String, WorkDoneDto> workDoneConsumer;

    @BeforeAll
    public void init() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        httpClient = HttpClients.createDefault();

        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092,localhost:29092,localhost:39092");
        producerProps.put(ProducerConfig.CLIENT_ID_CONFIG, "test");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        workRequestProducer = new KafkaProducer<>(producerProps);

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092,localhost:29092,localhost:39092");
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, WorkDoneDtoDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        workDoneConsumer = new KafkaConsumer<>(consumerProps);
        workDoneConsumer.subscribe(Collections.singleton("work-done-topic"));
    }

    @AfterAll
    public void cleanUp() {
        try {
            httpClient.close();
            workDoneConsumer.close();
            workRequestProducer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void treeConsumerWorkInParallel() throws Exception {
        ZonedDateTime from = ZonedDateTime.now();
        ZonedDateTime to = ZonedDateTime.now().plusMinutes(1L);
        final long[] usage = {100};
        CreateTokenRequest request = getRequest(from, to, usage[0]);
        String token = httpClient.execute(createRequest(request), tokenResponseHandler);
        for (int i = 0; i < usage[0]; i++) {
            workRequestProducer.send(new ProducerRecord<>("work-request-topic", 0, Integer.valueOf(i).toString(), token));
            Thread.sleep(2000l);
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
                        assertTrue(done.isResult());
                        int count = consumersUsage.getOrDefault(done.getClient(), 0);
                        consumersUsage.put(done.getClient(), ++count);
                        usage[0]--;
                    });
        }

        assertEquals(3, consumersUsage.size());
    }

    private HttpPost createRequest(CreateTokenRequest body) throws Exception {
        HttpPost httpPost = new HttpPost("http://localhost:48090/token");
        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(body));
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-type", "application/json");
        return httpPost;
    }

    private CreateTokenRequest getRequest(ZonedDateTime from, ZonedDateTime to, long usage) {
        CreateTokenRequest request = new CreateTokenRequest();
        request.setFrom(from);
        request.setTo(to);
        request.setUsage(usage);
        return request;
    }


}
