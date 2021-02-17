package com.leadspace.consumer.kafka;

public class Topics {
    private final String issuedTokenTopic;
    private final String usedTokenTopic;
    private final String workRequestTopic;
    private final String workDoneTopic;

    public Topics(String issuedTokenTopic, String usedTokenTopic, String workRequestTopic, String workDoneTopic) {
        assert issuedTokenTopic != null;
        assert usedTokenTopic != null;
        assert workRequestTopic != null;
        assert workDoneTopic != null;
        this.issuedTokenTopic = issuedTokenTopic;
        this.usedTokenTopic = usedTokenTopic;
        this.workRequestTopic = workRequestTopic;
        this.workDoneTopic = workDoneTopic;
    }

    public String getIssuedTokenTopic() {
        return issuedTokenTopic;
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
