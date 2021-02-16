package com.leadspace.issuer.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.leadspace.issuer.dto.TokenExpirationDto;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Properties;

@Configuration
public class KafkaFactory {

    @Bean
    public Producer getProducer(@Value("${kafka.bootstrapServer}") String bootstrapServer,
                                @Value("${kafka.clientId}") String clientId,
                                @Value("${kafka.issuedTokenTopic}") String topic) {
        KafkaProducer<String, TokenExpirationDto> producer = getKafkaProducer(bootstrapServer, clientId);
        return new Producer(producer, topic);
    }

    private KafkaProducer<String, TokenExpirationDto> getKafkaProducer(String bootstrapServer, String clientId) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TokenExpirationDtoSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    public static class TokenExpirationDtoSerializer implements Serializer<TokenExpirationDto> {

        private ObjectMapper objectMapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

        @Override
        public byte[] serialize(String topic, TokenExpirationDto data) {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Cant serialize TokenExpirationDto: " + data);
            }
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {

        }

        @Override
        public byte[] serialize(String topic, Headers headers, TokenExpirationDto data) {
            return serialize(topic, data);
        }

        @Override
        public void close() {
        }
    }
}
