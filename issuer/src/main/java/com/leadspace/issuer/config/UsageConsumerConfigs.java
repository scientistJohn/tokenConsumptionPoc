package com.leadspace.issuer.config;

public class UsageConsumerConfigs {
    private final String bootstrapServer;
    private final String groupId;
    private final String topic;

    public UsageConsumerConfigs(String bootstrapServer, String groupId, String topic) {
        this.bootstrapServer = bootstrapServer;
        this.groupId = groupId;
        this.topic = topic;
    }

    public String getBootstrapServer() {
        return bootstrapServer;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getTopic() {
        return topic;
    }
}
