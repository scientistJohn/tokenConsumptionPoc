package com.leadspace.consumer.kafka;

public class IssuedTokenConsumerConfig {
    private final String bootstrapServer;
    private final String issuedTokenTopic;
    private final String issuedTokenConsumerGroup;

    public IssuedTokenConsumerConfig(String bootstrapServer, String issuedTokenTopic, String issuedTokenConsumerGroup) {
        assert bootstrapServer != null;
        assert issuedTokenTopic != null;
        assert issuedTokenConsumerGroup != null;
        this.bootstrapServer = bootstrapServer;
        this.issuedTokenTopic = issuedTokenTopic;
        this.issuedTokenConsumerGroup = issuedTokenConsumerGroup;
    }

    public String getBootstrapServer() {
        return bootstrapServer;
    }

    public String getIssuedTokenTopic() {
        return issuedTokenTopic;
    }

    public String getIssuedTokenConsumerGroup() {
        return issuedTokenConsumerGroup;
    }
}
