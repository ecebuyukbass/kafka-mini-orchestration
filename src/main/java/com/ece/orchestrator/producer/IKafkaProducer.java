package com.ece.orchestrator.producer;

public interface IKafkaProducer {

    void sendMessage(String topic, Object message);

    void sendResponse(String topic, Object message);
}
