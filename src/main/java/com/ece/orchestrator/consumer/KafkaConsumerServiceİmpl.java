package com.ece.orchestrator.consumer;

import com.ece.orchestrator.client.ProtocolClient;
import com.ece.orchestrator.client.SellClient;
import com.ece.orchestrator.model.KafkaMessage;
import com.ece.orchestrator.service.InfrastructureService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.ece.orchestrator.producer.IKafkaProducer;
import com.ece.orchestrator.repository.IdempotencyRepository;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerServiceİmpl implements İKafkaConsumer {

    private final IKafkaProducer kafkaProducerService;
    private final IdempotencyRepository idempotencyRepository;
    @Autowired
    private InfrastructureService infrastructureService;

    private final ProtocolClient protocolClient;
    private final SellClient sellClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @KafkaListener(topics = "orchestrator-topic", groupId = "orchestrator-group")
    public void consume(String message) {
        try {
            log.info(" Kafka'dan mesaj alındı: {}", message);

            KafkaMessage kafkaMessage = objectMapper.readValue(message, KafkaMessage.class);

            if (!infrastructureService.validateTransaction(kafkaMessage)) {
                log.warn(" Infrastructure REDDETTİ: {}", kafkaMessage.getTransactionId());

                String failedResponse = String.format(
                        "{\"transactionId\": \"%s\", \"status\": \"FAILED\", \"message\": \"Transaction is duplicate or invalid.\"}",
                        kafkaMessage.getTransactionId()
                );
                kafkaProducerService.sendResponse("orchestrator-response-topic", failedResponse);
                return;
            }

            if (!infrastructureService.isSequential(kafkaMessage)) {
                log.warn(" Sıralı olmayan işlem reddedildi: {}", kafkaMessage.getTransactionId());
                String failedResponse = String.format(
                        "{\"transactionId\": \"%s\", \"status\": \"FAILED\", \"message\": \"Out-of-order transaction rejected.\"}",
                        kafkaMessage.getTransactionId()
                );
                kafkaProducerService.sendResponse("orchestrator-response-topic", failedResponse);
                return;
            }







            String finalStatus = "FAILED";

            String protocolResponse = protocolClient.sendToProtocol(kafkaMessage);
            if (protocolResponse.contains("SUCCESS")) {
                String sellResponse = sellClient.sendToSell(kafkaMessage);
                if (sellResponse.contains("SUCCESS")) {
                    finalStatus = "SUCCESS";
                }
            }

            String responseMessage = String.format("{\"transactionId\": \"%s\", \"status\": \"%s\", \"message\": \"Transaction processed successfully\"}",
                    kafkaMessage.getTransactionId(), finalStatus);

            kafkaProducerService.sendResponse("orchestrator-response-topic", responseMessage);
        } catch (Exception e) {
            log.error(" Kafka mesaj işlenirken hata oluştu: ", e);
        }
    }
}
