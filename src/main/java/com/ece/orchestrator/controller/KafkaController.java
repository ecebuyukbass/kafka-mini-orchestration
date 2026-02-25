package com.ece.orchestrator.controller;

import com.ece.orchestrator.model.KafkaMessage;

import com.ece.orchestrator.producer.KafkaProducerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaController {

    private final KafkaProducerServiceImpl kafkaProducerService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessageToKafka(@RequestBody KafkaMessage kafkaMessage) {
        String topic = "orchestrator-topic";
        kafkaProducerService.sendMessage(topic, kafkaMessage);
        return ResponseEntity.ok("Mesaj Kafka'ya gönderildi: " + kafkaMessage.getTransactionId());
    }
}
