package com.leadspace.issuer.config;

public class ValidTokenProducerConfigs {
    private final String bootstrapServer;
    private final String topic;
    private final String clientId;

    public ValidTokenProducerConfigs(String bootstrapServer, String topic, String clientId) {
        this.bootstrapServer = bootstrapServer;
        this.topic = topic;
        this.clientId = clientId;
    }

    public String getBootstrapServer() {
        return bootstrapServer;
    }

    public String getTopic() {
        return topic;
    }

    public String getClientId() {
        return clientId;
    }
}
