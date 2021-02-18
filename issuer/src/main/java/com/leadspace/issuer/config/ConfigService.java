package com.leadspace.issuer.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigService {
    private final Properties props;

    private ConfigService(Properties props) {
        this.props = props;
    }

    public static ConfigService create(String arg) {
        try (InputStream input = new FileInputStream(arg)) {
            Properties props = new Properties();
            props.load(input);
            return new ConfigService(props);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    public UsageConsumerConfigs geUsageConsumerConfigs() {
        return new UsageConsumerConfigs(props.getProperty("bootstrapServer"),
                props.getProperty("usedTokenGroup"),
                props.getProperty("usedTokenTopic"));
    }

    public ValidTokenProducerConfigs getValidTokenProducerConfigs(){
        return new ValidTokenProducerConfigs(props.getProperty("bootstrapServer"),
                props.getProperty("validTopic"),
                props.getProperty("clientId"));
    }

    public RequestConsumerConfig getRequestConsumerConfig() {
        return new RequestConsumerConfig(props.getProperty("bootstrapServer"),
                props.getProperty("tokenRequestGroup"),
                props.getProperty("tokenRequestTopic"));
    }
}
