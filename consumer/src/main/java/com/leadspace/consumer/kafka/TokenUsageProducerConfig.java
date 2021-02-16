package com.leadspace.consumer.kafka;

public class TokenUsageProducerConfig {
    private final String bootstrapServer;
    private final String clientId;
    private final String usedTokenTopic;

    public TokenUsageProducerConfig(String bootstrapServer, String clientId, String usedTokenTopic) {
        assert bootstrapServer != null;
        assert clientId != null;
        assert usedTokenTopic != null;
        this.bootstrapServer = bootstrapServer;
        this.clientId = clientId;
        this.usedTokenTopic = usedTokenTopic;
    }

    public String getBootstrapServer() {
        return bootstrapServer;
    }

    public String getClientId() {
        return clientId;
    }

    public String getUsedTokenTopic() {
        return usedTokenTopic;
    }
}
