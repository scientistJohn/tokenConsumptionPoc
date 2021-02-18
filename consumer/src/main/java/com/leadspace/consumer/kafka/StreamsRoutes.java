package com.leadspace.consumer.kafka;

import com.leadspace.consumer.dto.Pair;
import com.leadspace.consumer.serde.WorkDoneDtoDeserializer;
import com.leadspace.consumer.serde.WorkDoneDtoSerializer;
import com.leadspace.consumer.service.Worker;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import static org.apache.kafka.streams.KafkaStreams.State.RUNNING;

public class StreamsRoutes {
    private final StreamsBuilder builder = new StreamsBuilder();
    private final Worker worker;

    public StreamsRoutes(Worker worker) {
        this.worker = worker;
    }

    public void buildRoutes(Topics topics) {
        GlobalKTable<String, Integer> validTokensTable = buildRestrictionsTable(topics.getValidTokenTopic());

        KStream<String, String> workRequestStream = builder.stream(topics.getWorkRequestTopic(), Consumed.with(Serdes.String(), Serdes.String()))
                .peek((key, value) -> System.out.println("Work request token: " + key + " on value: " + value));

        KStream<String, Integer> isTokenUsageExceededStream = workRequestStream
                .join(validTokensTable, (k, v) -> k, Pair::new)
                .mapValues(Pair::getSecond)
                .peek((key, value) -> System.out.println("Joined token: " + key + " on valid: " + value));

        isTokenUsageExceededStream
                .mapValues(isExceeded -> isExceeded > 0 ? worker.workDone() : worker.workUndone())
                .peek((key, value) -> System.out.println("Work done token: " + key + " result: " + value.isResult()))
                .to(topics.getWorkDoneTopic(), Produced.with(Serdes.String(), Serdes.serdeFrom(new WorkDoneDtoSerializer(), new WorkDoneDtoDeserializer())));

        isTokenUsageExceededStream
                .filter((key, value) -> value == 1)
                .mapValues((key, value) -> key)
                .peek((key, value) -> System.out.println("Used token: " + value + " key: " + key))
                .to(topics.getUsedTokenTopic(), Produced.with(Serdes.String(), Serdes.String()));
    }

    private GlobalKTable<String, Integer> buildRestrictionsTable(String topic) {
        Consumed<String, Integer> serdes = Consumed.with(Serdes.String(), Serdes.Integer());
        return builder.globalTable(topic, serdes);
    }

    public void run(StreamsConfig config) throws Exception {
        final KafkaStreams streams = new KafkaStreams(builder.build(), getStreamsProps(config));
        CompletableFuture<Void> startedStreams = new CompletableFuture<>();
        streams.setStateListener((newState, oldState) -> {
            if (newState == RUNNING) {
                startedStreams.complete(null);
            }
        });
        streams.cleanUp();
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        startedStreams.get();
    }

    private Properties getStreamsProps(StreamsConfig config) {
        return new Properties() {
            {
                put(org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG, config.getApplicationId());
                put(org.apache.kafka.streams.StreamsConfig.CLIENT_ID_CONFIG, config.getClientId());
                put(org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServer());
                put(org.apache.kafka.streams.StreamsConfig.STATE_DIR_CONFIG, config.getStateDirConfig());
            }
        };
    }
}
