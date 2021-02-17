package com.leadspace.consumer.service;

import com.leadspace.consumer.dto.WorkDoneDto;

import java.util.Random;

public class Worker {
    private final Random random = new Random();
    private final String instanceId;

    public Worker(WorkerConfigs configs) {
        this.instanceId = configs.getInstanceId();
    }

    public WorkDoneDto workDone() {
        try {
            Thread.sleep(Math.abs(random.nextLong() % 1000L));
            return new WorkDoneDto(instanceId, true);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public WorkDoneDto workUndone() {
        return new WorkDoneDto(instanceId, false);
    }
}
