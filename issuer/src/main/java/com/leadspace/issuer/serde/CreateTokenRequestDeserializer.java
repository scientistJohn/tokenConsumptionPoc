package com.leadspace.issuer.serde;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadspace.issuer.dto.CreateTokenRequest;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

public class CreateTokenRequestDeserializer implements Deserializer<CreateTokenRequest> {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Override
    public CreateTokenRequest deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, CreateTokenRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Cant serialize WorkDoneDto for topic:" + topic);
        }
    }

    @Override
    public CreateTokenRequest deserialize(String topic, Headers headers, byte[] data) {
        return deserialize(topic, data);
    }

    @Override
    public void close() {
    }
}
