package com.leadspace.consumer.kafka;

import com.leadspace.consumer.dto.Pair;
import com.leadspace.consumer.dto.TokenExpirationDto;
import com.leadspace.consumer.serde.TokenExpirationDtoDeserializer;
import com.leadspace.consumer.serde.TokenExpirationDtoSerializer;
import com.leadspace.consumer.serde.WorkDoneDtoDeserializer;
import com.leadspace.consumer.serde.WorkDoneDtoSerializer;
import com.leadspace.consumer.service.Worker;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import static org.apache.kafka.streams.KafkaStreams.State.RUNNING;

public class StreamsRoutes {
    final StreamsBuilder builder = new StreamsBuilder();
    private final Worker worker;

    public StreamsRoutes(Worker worker) {
        this.worker = worker;
    }

    public void buildRoutes(Topics topics) {
        KTable<String, TokenExpirationDto> restrictionsTable = buildRestrictionsTable(topics.getIssuedTokenTopic());
        KStream<String, Long> usageStream = builder.stream(topics.getUsedTokenTopic(), Consumed.with(Serdes.String(), Serdes.Long()))
                .peek((key, value) -> System.out.println("Used token: " + key + "on timestamp: " + value ));
        KStream<String, String> workRequestStream = builder.stream(topics.getWorkRequestTopic(), Consumed.with(Serdes.String(), Serdes.String()))
                .peek((key, value) -> System.out.println("Work request token: " + key + "on value: " + value ));


        KTable<String, Long> usageCountTable = usageStream
                .join(restrictionsTable, Pair::new)
                .filter((key, value) -> {
                    TokenExpirationDto expirationDto = value.getSecond();
                    Long used = value.getFirst();
                    return expirationDto.getFrom() <= used
                            && expirationDto.getTo() >= used;
                })
                .groupByKey()
                .count();

        KTable<String, Integer> limitExcitedTable = restrictionsTable
                .leftJoin(usageCountTable, Pair::new)
                .mapValues(pair -> {
                    long allowedUsage = pair.getFirst().getUsage();
                    long realUsage = pair.getSecond() == null ? 0 : pair.getSecond();
                    return allowedUsage > realUsage ? 1 : 0;
                });

        KStream<String, Integer> isTokenUsageExceededStream = workRequestStream
                .join(limitExcitedTable, Pair::new)
                .mapValues(Pair::getSecond);

        isTokenUsageExceededStream
                .mapValues(isExceeded -> isExceeded > 0 ? worker.workDone() : worker.workUndone())
                .to(topics.getWorkDoneTopic(), Produced.with(Serdes.String(), Serdes.serdeFrom(new WorkDoneDtoSerializer(), new WorkDoneDtoDeserializer())));

        isTokenUsageExceededStream
                .filter((key, value) -> value == 1)
                .mapValues(value -> Instant.now().getEpochSecond())
                .to(topics.getUsedTokenTopic(), Produced.with(Serdes.String(), Serdes.Long()));
    }

    private KTable<String, TokenExpirationDto> buildRestrictionsTable(String topic) {
        Serde<TokenExpirationDto> tokenExpirationDtoSerde = Serdes.serdeFrom(new TokenExpirationDtoSerializer(), new TokenExpirationDtoDeserializer());
        Consumed<String, TokenExpirationDto> serdes = Consumed.with(Serdes.String(), tokenExpirationDtoSerde);
        return builder.table(topic, serdes);
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
