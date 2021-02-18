package com.leadspace.issuer;

import com.leadspace.issuer.config.ConfigService;
import com.leadspace.issuer.kafka.KafkaFactory;
import com.leadspace.issuer.kafka.RequestConsumer;
import com.leadspace.issuer.kafka.UsageConsumer;
import com.leadspace.issuer.kafka.ValidTokenProducer;
import com.leadspace.issuer.service.SchedulerService;
import com.leadspace.issuer.service.TokenService;
import com.leadspace.issuer.service.UsageService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Application {

    public static void main(String[] args) {
        String propertiesFile = args.length > 0 ? args[0] : "application.properties";
        ConfigService configService = ConfigService.create(propertiesFile);
        ValidTokenProducer validTokenProducer = KafkaFactory.getTokenValidationProducer(configService.getValidTokenProducerConfigs());
        UsageService usageService = new UsageService(validTokenProducer);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
        UsageConsumer usageConsumer = KafkaFactory.getUsageConsumer(configService.geUsageConsumerConfigs(), usageService, executorService);
        SchedulerService schedulerService = new SchedulerService(executorService);
        TokenService tokenService = new TokenService(usageService, schedulerService, usageConsumer);
        RequestConsumer requestConsumer = KafkaFactory.getRequestsConsumer(configService.getRequestConsumerConfig(), tokenService);

        try {
            while (true) {
                usageConsumer.consume();
                requestConsumer.consume();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            shutDown(executorService);
            System.out.println("\nFinished all threads");
        }
    }

    public static void shutDown(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
