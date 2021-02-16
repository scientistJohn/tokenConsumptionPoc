package com.leadspace.issuer.service;

import com.leadspace.issuer.dto.CreateTokenRequest;
import com.leadspace.issuer.dto.TokenExpirationDto;
import com.leadspace.issuer.kafka.Producer;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenIssuerService {
    private final Producer producer;

    public TokenIssuerService(Producer producer) {
        this.producer = producer;
    }

    public String publishToken(CreateTokenRequest request) {
        String token = UUID.randomUUID().toString();
        TokenExpirationDto expirationDto = createTokenExpirationDto(token, request);
        producer.send(token, expirationDto);
        return token;
    }

    private TokenExpirationDto createTokenExpirationDto(String token, CreateTokenRequest request) {
        TokenExpirationDto expirationDto = new TokenExpirationDto();
        expirationDto.setToken(token);
        expirationDto.setTo(request.getTo().toEpochSecond());
        expirationDto.setFrom(request.getFrom().toEpochSecond());
        expirationDto.setUsage(request.getUsage());
        return expirationDto;
    }
}
