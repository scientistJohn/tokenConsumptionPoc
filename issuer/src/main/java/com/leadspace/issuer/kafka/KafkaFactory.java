package com.leadspace.issuer.kafka;

import com.leadspace.issuer.config.RequestConsumerConfig;
import com.leadspace.issuer.config.UsageConsumerConfigs;
import com.leadspace.issuer.config.ValidTokenProducerConfigs;
import com.leadspace.issuer.dto.CreateTokenRequest;
import com.leadspace.issuer.serde.CreateTokenRequestDeserializer;
import com.leadspace.issuer.service.TokenService;
import com.leadspace.issuer.service.UsageService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public final class KafkaFactory {

    private KafkaFactory() {
        throw new UnsupportedOperationException();
    }

    public static ValidTokenProducer getTokenValidationProducer(ValidTokenProducerConfigs configs) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, configs.getBootstrapServer());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, configs.getClientId());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        KafkaProducer<String, Integer> producer = new KafkaProducer<>(props);
        return new ValidTokenProducer(producer, configs.getTopic());
    }

    public static RequestConsumer getRequestsConsumer(RequestConsumerConfig config, TokenService tokenService) {
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServer());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, config.getGroupId());
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CreateTokenRequestDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        KafkaConsumer<String, CreateTokenRequest> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singleton(config.getTopic()));
        return new RequestConsumer(consumer, tokenService);
    }

    public static UsageConsumer getUsageConsumer(UsageConsumerConfigs config, UsageService usageService, ExecutorService executorService) {
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServer());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, config.getGroupId());
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singleton(config.getTopic()));
        return new UsageConsumer(consumer, usageService, executorService);
    }


}
