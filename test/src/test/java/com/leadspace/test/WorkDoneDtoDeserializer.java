package com.leadspace.test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

public class WorkDoneDtoDeserializer implements Deserializer<WorkDoneDto> {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Override
    public WorkDoneDto deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, WorkDoneDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Cant serialize WorkDoneDto for topic:" + topic);
        }
    }

    @Override
    public WorkDoneDto deserialize(String topic, Headers headers, byte[] data) {
        return deserialize(topic, data);
    }

    @Override
    public void close() {
    }
}
