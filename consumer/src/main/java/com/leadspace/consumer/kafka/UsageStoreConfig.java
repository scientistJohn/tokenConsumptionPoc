package com.leadspace.consumer.kafka;

public class UsageStoreConfig {
    private final String bootstrapServer;
    private final String stateDirConfig;
    private final String applicationId;
    private final String clientId;
    private final String usedTokenTopic;
    private final String usageStoreName;

    public UsageStoreConfig(String bootstrapServer, String stateDirConfig, String applicationId, String clientId, String usedTokenTopic, String usageStoreName) {
        assert bootstrapServer != null;
        assert stateDirConfig != null;
        assert applicationId != null;
        assert clientId != null;
        assert usedTokenTopic != null;
        assert usageStoreName != null;
        this.bootstrapServer = bootstrapServer;
        this.stateDirConfig = stateDirConfig;
        this.applicationId = applicationId;
        this.clientId = clientId;
        this.usedTokenTopic = usedTokenTopic;
        this.usageStoreName = usageStoreName;
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

    public String getUsedTokenTopic() {
        return usedTokenTopic;
    }

    public String getUsageStoreName() {
        return usageStoreName;
    }
}
