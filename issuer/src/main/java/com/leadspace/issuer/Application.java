package com.leadspace.issuer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(25000L);
        SpringApplication.run(Application.class, args);
    }
}
