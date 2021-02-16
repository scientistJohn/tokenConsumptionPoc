package com.leadspace.consumer.service;

import java.util.Random;

public class Worker {
    private final Random random = new Random();

    public void work() {
        try {
            Thread.sleep(Math.abs(random.nextLong() % 1000L));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
