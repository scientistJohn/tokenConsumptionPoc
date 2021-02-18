package com.leadspace.issuer.service;

import com.leadspace.issuer.dto.CreateTokenRequest;
import com.leadspace.issuer.dto.TokenInfo;
import com.leadspace.issuer.kafka.ValidTokenProducer;

import java.util.*;

public class UsageService {
    private final Map<String, TokenInfo> tokens = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Long> tokenUsage = Collections.synchronizedMap(new HashMap<>());
    private final ValidTokenProducer validTokenProducer;

    public UsageService(ValidTokenProducer validTokenProducer) {
        this.validTokenProducer = validTokenProducer;
    }

    public void handleUsage(String token, long timestamp) {
        long usage = updateUsage(token, 1);
        if (isValidToken(token, timestamp, usage)) {
            validTokenProducer.sendValid(token);
        } else {
            System.out.println("Illegal usage of expired token: " + token);
            validTokenProducer.sendInvalid(token);
        }
    }

    private long updateUsage(String token, int increment) {
        long usage = tokenUsage.getOrDefault(token, 0L);
        tokenUsage.put(token, usage += increment);
        return usage;
    }

    private boolean isValidToken(String token, Long timestamp, long usage) {
        boolean contains = tokens.containsKey(token);
        System.out.println("Contains" + contains);
        if (!contains) {
            return false;
        }
        TokenInfo expirationDto = tokens.get(token);
        boolean greaterThanFrom = expirationDto.getFrom() <= timestamp;
        boolean lessThanTo = expirationDto.getTo() > timestamp;
        boolean lessThanUsage = usage < expirationDto.getUsage();
        System.out.println("greaterThanFrom: " + greaterThanFrom);
        System.out.println("lessThanTo: " + lessThanTo);
        System.out.println("lessThanUsage: " + lessThanUsage);
        return expirationDto.getFrom() <= timestamp
                && expirationDto.getTo() > timestamp
                && usage < expirationDto.getUsage();
    }

    public void handleUsage(Map<String, List<Long>> usageMap) {
        usageMap.forEach((token, timestamps) -> {
            long usage = updateUsage(token, timestamps.size());
            Optional<Long> illegal = timestamps.stream()
                    .filter(timestamp -> isValidToken(token, timestamp, usage))
                    .peek(timestamp -> System.out.println("Illegal usage of expired token: " + token))
                    .findAny();
            if (illegal.isPresent()) {
                validTokenProducer.sendValid(token);
            } else {
                validTokenProducer.sendInvalid(token);
            }
        });
    }

    public TokenInfo handleCreate(CreateTokenRequest request) {
        TokenInfo tokenInfo = handleUpdate(request);
        validTokenProducer.sendValid(tokenInfo.getToken());
        return tokenInfo;
    }

    public TokenInfo handleUpdate(CreateTokenRequest request) {
        TokenInfo tokenInfo = constructToken(request);
        tokens.put(tokenInfo.getToken(), tokenInfo);
        tokenUsage.put(tokenInfo.getToken(), 0L);
        return tokenInfo;
    }

    private TokenInfo constructToken(CreateTokenRequest request) {
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(request.getToken());
        tokenInfo.setTo(request.getPeriod().nextPeriod());
        tokenInfo.setFrom(request.getPeriod().currPeriod());
        tokenInfo.setUsage(request.getUsage());
        tokenInfo.setPeriod(request.getPeriod());
        return tokenInfo;
    }

    public void handleDelete(String token) {
        tokens.remove(token);
        tokenUsage.remove(token);
        validTokenProducer.sendInvalid(token);
    }
}
