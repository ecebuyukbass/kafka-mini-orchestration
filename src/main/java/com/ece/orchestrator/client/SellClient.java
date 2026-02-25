package com.ece.orchestrator.client;

import com.ece.orchestrator.model.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellClient {

    private final WebClient webClient = WebClient.create("http://localhost:8089");
    @Retryable(
            value = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String sendToSell(KafkaMessage message) {
        log.info(" Sell Servisine mesaj gönderiliyor: {}", message.getTransactionId());

        return webClient.post()
                .uri("/sell/process")
                .bodyValue(message)
                .retrieve()
                .onStatus(status -> status.isError(), response ->
                        response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("HTTP ERROR: " + body))
                )
                .bodyToMono(String.class)
                .block();

    }

    @Recover
    public String recover(Exception e, KafkaMessage message) {
        log.error("Retry işlemi başarısız oldu. Geriye düşüldü. Mesaj: {}", message);
        return "FAILED";
    }
}
