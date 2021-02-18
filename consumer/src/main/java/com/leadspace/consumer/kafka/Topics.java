package com.leadspace.consumer.kafka;

public class Topics {
    private final String validTokenTopic;
    private final String usedTokenTopic;
    private final String workRequestTopic;
    private final String workDoneTopic;

    public Topics(String validTokenTopic, String usedTokenTopic, String workRequestTopic, String workDoneTopic) {
        assert validTokenTopic != null;
        assert usedTokenTopic != null;
        assert workRequestTopic != null;
        assert workDoneTopic != null;
        this.validTokenTopic = validTokenTopic;
        this.usedTokenTopic = usedTokenTopic;
        this.workRequestTopic = workRequestTopic;
        this.workDoneTopic = workDoneTopic;
    }

    public String getValidTokenTopic() {
        return validTokenTopic;
    }

    public String getUsedTokenTopic() {
        return usedTokenTopic;
    }

    public String getWorkRequestTopic() {
        return workRequestTopic;
    }

    public String getWorkDoneTopic() {
        return workDoneTopic;
    }
}
