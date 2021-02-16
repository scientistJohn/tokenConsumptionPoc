package com.leadspace.consumer.config;

import com.leadspace.consumer.kafka.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesService {
    private final Properties props;

    private PropertiesService(Properties props) {
        this.props = props;
    }

    public static PropertiesService create(String arg) {
        try (InputStream input = new FileInputStream(arg)) {
            Properties props = new Properties();
            props.load(input);
            return new PropertiesService(props);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    public UsageStoreConfig getUsageStoreConfig() {
        String bootstrapServer = props.getProperty("bootstrapServer");
        String stateDirConfig = props.getProperty("stateDirConfig");
        String applicationId = props.getProperty("applicationId");
        String clientId = props.getProperty("clientId");
        String usedTokenTopic = props.getProperty("usedTokenTopic");
        String usageStoreName = props.getProperty("usageStoreName");
        return new UsageStoreConfig(bootstrapServer, stateDirConfig, applicationId, clientId, usedTokenTopic, usageStoreName);
    }

    public IssuedTokenConsumerConfig getIssuedTokenConsumerConfig() {
        String bootstrapServer = props.getProperty("bootstrapServer");
        String issuedTokenTopic = props.getProperty("issuedTokenTopic");
        String issuedTokenConsumerGroup = props.getProperty("issuedTokenConsumerGroup");
        return new IssuedTokenConsumerConfig(bootstrapServer, issuedTokenTopic, issuedTokenConsumerGroup);
    }

    public WorkRequestConsumerConfig getWorkRequestConsumerConfig() {
        String workRequestTopic = props.getProperty("workRequestTopic");
        String workRequestConsumerGroup = props.getProperty("workRequestConsumerGroup");
        String bootstrapServer = props.getProperty("bootstrapServer");
        return new WorkRequestConsumerConfig(workRequestTopic, workRequestConsumerGroup, bootstrapServer);
    }

    public TokenUsageProducerConfig getTokenUsageProducerConfig() {
        String bootstrapServer = props.getProperty("bootstrapServer");
        String clientId = props.getProperty("clientId");
        String usedTokenTopic = props.getProperty("usedTokenTopic");
        return new TokenUsageProducerConfig(bootstrapServer, clientId, usedTokenTopic);
    }

    public WorkDoneProducerConfig getWorkDoneProducerConfig() {
        String bootstrapServer = props.getProperty("bootstrapServer");
        String clientId = props.getProperty("clientId");
        String workDoneTopic = props.getProperty("workDoneTopic");
        return new WorkDoneProducerConfig(bootstrapServer, clientId, workDoneTopic);
    }
}
