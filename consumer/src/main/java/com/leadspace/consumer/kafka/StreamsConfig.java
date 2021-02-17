package com.leadspace.consumer.kafka;

public class StreamsConfig {
    private final String bootstrapServer;
    private final String stateDirConfig;
    private final String applicationId;
    private final String clientId;

    public StreamsConfig(String bootstrapServer, String stateDirConfig, String applicationId, String clientId) {
        assert bootstrapServer != null;
        assert stateDirConfig != null;
        assert applicationId != null;
        assert clientId != null;
        this.bootstrapServer = bootstrapServer;
        this.stateDirConfig = stateDirConfig;
        this.applicationId = applicationId;
        this.clientId = clientId;
    }

    public String getBootstrapServer() {
        return bootstrapServer;
    }

    public String getStateDirConfig() {
        return stateDirConfig;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getClientId() {
        return clientId;
    }
}
