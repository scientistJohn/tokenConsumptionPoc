package com.leadspace.consumer.serde;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadspace.consumer.dto.TokenExpirationDto;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

public class TokenExpirationDtoDeserializer implements Deserializer<TokenExpirationDto> {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Override
    public TokenExpirationDto deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, TokenExpirationDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Cant serialize TokenExpirationDto for topic:" + topic);
        }
    }

    @Override
    public TokenExpirationDto deserialize(String topic, Headers headers, byte[] data) {
        return deserialize(topic, data);
    }

    @Override
    public void close() {
    }
}
