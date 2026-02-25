package com.ece.orchestrator.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements IKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void sendMessage(String topic, Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            log.info(" Kafka'ya mesaj gönderiliyor: Topic={}, Mesaj={}", topic, json);
            kafkaTemplate.send(topic, json);
        } catch (Exception e) {
            log.error(" Kafka'ya mesaj gönderilirken hata oluştu!", e);
        }
    }

    @Override
    public void sendResponse(String topic, Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            log.info(" Kafka'ya yanıt gönderiliyor: Topic={}, Mesaj={}", topic, json);
            kafkaTemplate.send(topic, json);
        } catch (Exception e) {
            log.error("Kafka'ya yanıt gönderilirken hata oluştu!", e);
        }
    }
}
