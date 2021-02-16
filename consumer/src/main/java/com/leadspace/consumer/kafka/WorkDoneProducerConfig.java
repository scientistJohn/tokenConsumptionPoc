package com.leadspace.consumer.kafka;

public class WorkDoneProducerConfig {
    private final String bootstrapServer;
    private final String clientId;
    private final String workDoneTopic;

    public WorkDoneProducerConfig(String bootstrapServer, String clientId, String workDoneTopic) {
        assert bootstrapServer != null;
        assert clientId != null;
        assert workDoneTopic != null;
        this.bootstrapServer = bootstrapServer;
        this.clientId = clientId;
        this.workDoneTopic = workDoneTopic;
    }

    public String getBootstrapServer() {
        return bootstrapServer;
    }

    public String getClientId() {
        return clientId;
    }

    public String getWorkDoneTopic() {
        return workDoneTopic;
    }
}
