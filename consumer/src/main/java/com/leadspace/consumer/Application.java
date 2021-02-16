package com.leadspace.consumer;

import com.leadspace.consumer.config.PropertiesService;
import com.leadspace.consumer.kafka.*;
import com.leadspace.consumer.service.ProcessingService;
import com.leadspace.consumer.service.Worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Application {

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(20000L);
        String propertiesFile = args.length > 0 ? args[0] : "application.properties";
        PropertiesService propertiesService = PropertiesService.create(propertiesFile);
        UsageStore usageStore = KafkaFactory.createUsageStore(propertiesService.getUsageStoreConfig());
        TokenUsageProducer tokenUsageProducer = KafkaFactory.createTokenUsageProducer(propertiesService.getTokenUsageProducerConfig());
        WorkDoneProducer workDoneProducer = KafkaFactory.createWorkDoneProducer(propertiesService.getWorkDoneProducerConfig());
        Worker worker = new Worker();
        ProcessingService processingService = new ProcessingService(usageStore, tokenUsageProducer, workDoneProducer, worker);

        ExecutorService executor = Executors.newCachedThreadPool();
        WorkRequestConsumer workRequestConsumer = KafkaFactory.createWorkRequestConsumer(propertiesService.getWorkRequestConsumerConfig(), processingService, executor);
        IssuedTokenConsumer issuedTokenConsumer = KafkaFactory.createIssuedTokenConsumer(propertiesService.getIssuedTokenConsumerConfig(), processingService, executor);

        try {
            while (true) {
                issuedTokenConsumer.consume();
                workRequestConsumer.consume();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            shutDown(executor);
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
