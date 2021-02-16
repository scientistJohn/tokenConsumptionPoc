package com.leadspace.consumer.kafka;

public class WorkRequestConsumerConfig {
    private final String workRequestTopic;
    private final String workRequestConsumerGroup;
    private final String bootstrapServer;

    public WorkRequestConsumerConfig(String workRequestTopic, String workRequestConsumerGroup, String bootstrapServer) {
        assert workRequestTopic != null;
        assert workRequestConsumerGroup != null;
        assert bootstrapServer != null;
        this.workRequestTopic = workRequestTopic;
        this.workRequestConsumerGroup = workRequestConsumerGroup;
        this.bootstrapServer = bootstrapServer;
    }

    public String getWorkRequestTopic() {
        return workRequestTopic;
    }

    public String getWorkRequestConsumerGroup() {
        return workRequestConsumerGroup;
    }

    public String getBootstrapServer() {
        return bootstrapServer;
    }
}
