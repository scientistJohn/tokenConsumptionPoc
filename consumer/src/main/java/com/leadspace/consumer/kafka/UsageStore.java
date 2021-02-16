package com.leadspace.consumer.kafka;

import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;

import java.time.Instant;

public class UsageStore {
    private final ReadOnlyWindowStore<String, Long> store;

    public UsageStore(ReadOnlyWindowStore<String, Long> store) {
        this.store = store;
    }

    public long getUsage(String token, Instant from, Instant to) {
        long usage = 0L;
        WindowStoreIterator<Long> iterator = store.fetch(token, from, to);
        while (iterator.hasNext()) {
            usage += iterator.next().value;
        }
        iterator.close();
        return usage;
    }
}
