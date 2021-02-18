package com.leadspace.issuer.kafka;

import com.leadspace.issuer.dto.CreateTokenRequest;
import com.leadspace.issuer.dto.RequestAction;
import com.leadspace.issuer.service.TokenService;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;

public class RequestConsumer {
    private final KafkaConsumer<String, CreateTokenRequest> consumer;
    private final TokenService tokenService;

    public RequestConsumer(KafkaConsumer<String, CreateTokenRequest> consumer, TokenService tokenService) {
        this.consumer = consumer;
        this.tokenService = tokenService;
    }

    public void consume() {
        consumer.poll(Duration.ofSeconds(2L))
                .forEach(r -> processRecord(r.key(), r.value()));
    }

    private void processRecord(String action, CreateTokenRequest request) {
        try {
            switch (RequestAction.valueOf(action)) {
                case POST:
                    tokenService.createToken(request);
                    break;
                case PUT:
                    tokenService.updateToken(request);
                    break;
                case DELETE:
                    tokenService.deleteToken(request.getToken());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
