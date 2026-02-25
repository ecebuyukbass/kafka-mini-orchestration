package com.ece.orchestrator.client;

import com.ece.orchestrator.model.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProtocolClient {

    private final WebClient webClient = WebClient.create("http://localhost:8089");

    @Retryable(
            value = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )

    public String sendToProtocol(KafkaMessage message) {
        log.info(" Infrastructure Protocol servisine mesaj gönderiliyor: {}", message.getTransactionId());

        return webClient.post()
                .uri("/protocol/process")
                .bodyValue(message)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


    @Recover
    public String recover(Exception e, KafkaMessage message) {
        log.error("Retry işlemi başarısız oldu. Geriye düşüldü. Mesaj: {}", message);
        return "FAILED";
    }
}

