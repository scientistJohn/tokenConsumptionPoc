package com.leadspace.issuer.service;

import com.leadspace.issuer.dto.TokenInfo;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SchedulerService {
    private final Map<String, ScheduledFuture> jobs = new HashMap<>();
    private final ScheduledExecutorService scheduler;

    public SchedulerService(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    public void schedule(Runnable runnable, TokenInfo tokenInfo) {
        if (jobs.containsKey(tokenInfo.getToken())) {
            jobs.get(tokenInfo.getToken()).cancel(true);
        }
        long delay = tokenInfo.getTo() - Instant.now().getEpochSecond();
        long period = tokenInfo.getPeriod().getDurationInSeconds();
        ScheduledFuture future = scheduler.scheduleAtFixedRate(runnable, delay, period, TimeUnit.SECONDS);
        jobs.put(tokenInfo.getToken(), future);
    }
}
