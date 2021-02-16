package com.leadspace.consumer.kafka;

import com.leadspace.consumer.dto.TokenExpirationDto;
import com.leadspace.consumer.dto.WorkDoneDto;
import com.leadspace.consumer.service.ProcessingService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static org.apache.kafka.streams.KafkaStreams.State.RUNNING;

public final class KafkaFactory {
    private KafkaFactory() {
        throw new UnsupportedOperationException();
    }

    public static UsageStore createUsageStore(UsageStoreConfig config) {
        try {
            final StreamsBuilder builder = new StreamsBuilder();

            builder.stream(config.getUsedTokenTopic(), Consumed.with(Serdes.String(), Serdes.String()))
                    .groupBy((key, value) -> key, Grouped.with(Serdes.String(), Serdes.String()))
                    .windowedBy(TimeWindows.of(Duration.ofMinutes(1L)))
                    .count(Materialized.as(config.getUsageStoreName()));

            final KafkaStreams streams = new KafkaStreams(builder.build(), getStreamsProps(config));
            CompletableFuture<KafkaStreams> startedStreams = new CompletableFuture<>();
            streams.setStateListener((newState, oldState) -> {
                if (newState == RUNNING) {
                    startedStreams.complete(streams);
                }
            });
            streams.cleanUp();
            streams.start();

            Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

            startedStreams.get();
            ReadOnlyWindowStore<String, Long> store = streams.store(StoreQueryParameters.fromNameAndType(config.getUsageStoreName(), QueryableStoreTypes.windowStore()));
            //store.
            return new UsageStore(store);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static Properties getStreamsProps(UsageStoreConfig config) {
        return new Properties() {
            {
                put(org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG, config.getApplicationId());
                put(org.apache.kafka.streams.StreamsConfig.CLIENT_ID_CONFIG, config.getClientId());
                put(org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServer());
                put(org.apache.kafka.streams.StreamsConfig.STATE_DIR_CONFIG, config.getStateDirConfig());
            }
        };
    }

    public static IssuedTokenConsumer createIssuedTokenConsumer(IssuedTokenConsumerConfig config, ProcessingService processingService, ExecutorService executor) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServer());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, config.getIssuedTokenConsumerGroup());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TokenExpirationDtoDeserializer.class.getName());

        Consumer<String, TokenExpirationDto> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singleton(config.getIssuedTokenTopic()));
        return new IssuedTokenConsumer(consumer, processingService, executor);
    }

    public static TokenUsageProducer createTokenUsageProducer(TokenUsageProducerConfig config) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServer());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, config.getClientId());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        return new TokenUsageProducer(producer, config.getUsedTokenTopic(), config.getClientId());
    }

    public static WorkDoneProducer createWorkDoneProducer(WorkDoneProducerConfig config) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServer());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, config.getClientId());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, WorkDoneDtoSerializer.class.getName());
        KafkaProducer<String, WorkDoneDto> producer = new KafkaProducer<>(props);
        return new WorkDoneProducer(producer, config.getWorkDoneTopic(), config.getClientId());
    }

    public static WorkRequestConsumer createWorkRequestConsumer(WorkRequestConsumerConfig config, ProcessingService processingService, ExecutorService executor) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServer());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, config.getWorkRequestConsumerGroup());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singleton(config.getWorkRequestTopic()));
        return new WorkRequestConsumer(consumer, processingService, executor);
    }
}
