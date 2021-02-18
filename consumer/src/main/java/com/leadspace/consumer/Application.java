package com.leadspace.consumer;

import com.leadspace.consumer.config.PropertiesService;
import com.leadspace.consumer.kafka.StreamsRoutes;
import com.leadspace.consumer.service.Worker;

public class Application {

    public static void main(String[] args) {
        String propertiesFile = args.length > 0 ? args[0] : "application.properties";
        PropertiesService propertiesService = PropertiesService.create(propertiesFile);
        Worker worker = new Worker(propertiesService.getWorkerConfigs());
        StreamsRoutes routes = new StreamsRoutes(worker);
        routes.buildRoutes(propertiesService.getTopics());
        try {
            routes.run(propertiesService.getStreamsConfig());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
