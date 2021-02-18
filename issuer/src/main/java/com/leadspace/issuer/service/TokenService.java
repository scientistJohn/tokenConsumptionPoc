package com.leadspace.issuer.service;

import com.leadspace.issuer.dto.CreateTokenRequest;
import com.leadspace.issuer.dto.TokenInfo;
import com.leadspace.issuer.kafka.UsageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TokenService {
    private final Logger log = LoggerFactory.getLogger(TokenService.class);

    private final UsageService usageService;
    private final SchedulerService scheduler;
    private final UsageConsumer usageConsumer;

    public TokenService(UsageService usageService, SchedulerService scheduler, UsageConsumer usageConsumer) {
        this.usageService = usageService;
        this.scheduler = scheduler;
        this.usageConsumer = usageConsumer;
    }

    public void createToken(CreateTokenRequest request) {
        TokenInfo tokenInfo = usageService.handleCreate(request);
        scheduler.schedule(() -> usageService.handleCreate(request), tokenInfo);
    }

    public void updateToken(CreateTokenRequest request) {
        TokenInfo tokenInfo = usageService.handleUpdate(request);
        usageConsumer.resetConsumer(tokenInfo.getFrom());
        scheduler.schedule(() -> usageService.handleCreate(request), tokenInfo);
    }

    public void deleteToken(String token) {
        usageService.handleDelete(token);
    }
}
