package com.leadspace.consumer.service;

public class WorkerConfigs {
    private final String instanceId;

    public WorkerConfigs(String instanceId) {
        assert instanceId != null;
        this.instanceId = instanceId;
    }

    public String getInstanceId() {
        return instanceId;
    }
}
