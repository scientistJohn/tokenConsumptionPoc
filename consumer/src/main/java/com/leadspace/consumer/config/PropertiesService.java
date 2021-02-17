package com.leadspace.consumer.config;

import com.leadspace.consumer.kafka.StreamsConfig;
import com.leadspace.consumer.kafka.Topics;
import com.leadspace.consumer.service.WorkerConfigs;

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

    public StreamsConfig getStreamsConfig() {
        String bootstrapServer = props.getProperty("bootstrapServer");
        String stateDirConfig = props.getProperty("stateDirConfig");
        String applicationId = props.getProperty("applicationId");
        String clientId = props.getProperty("clientId");
        return new StreamsConfig(bootstrapServer, stateDirConfig, applicationId, clientId);
    }

    public Topics getTopics() {
        String issuedTokenTopic = props.getProperty("issuedTokenTopic");
        String usedTokenTopic = props.getProperty("usedTokenTopic");
        String workRequestTopic = props.getProperty("workRequestTopic");
        String workDoneTopic = props.getProperty("workDoneTopic");
        return new Topics(issuedTokenTopic, usedTokenTopic, workRequestTopic, workDoneTopic);
    }

    public WorkerConfigs getWorkerConfigs() {
        String instanceId = props.getProperty("instanceId");
        return new WorkerConfigs(instanceId);
    }
}
