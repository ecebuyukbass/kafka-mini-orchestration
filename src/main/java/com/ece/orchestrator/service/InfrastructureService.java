package com.ece.orchestrator.service;


import com.ece.orchestrator.model.KafkaMessage;
import com.ece.orchestrator.repository.IdempotencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InfrastructureService {

    private final IdempotencyRepository idempotencyRepository;

    private final Map<Integer, String> lastTransactionMap = new HashMap<>();

    public boolean validateTransaction(KafkaMessage message) {
        String transactionId = message.getTransactionId();

        if (idempotencyRepository.isDuplicateTransaction(transactionId)) {
            log.warn("zaten işlenmiş", transactionId);
            return false;
        }
        log.info(" Yeni işlem kabul edildi, Redis'e yazılıyor: {}", transactionId);
        idempotencyRepository.save(transactionId);
        return true;

    }

    public boolean isSequential(KafkaMessage message) {
        int terminalId = message.getTerminalId();
        String newTransactionId = message.getTransactionId();

        String lastTransactionId = lastTransactionMap.get(terminalId);

        if (lastTransactionId != null && newTransactionId.compareTo(lastTransactionId) <= 0) {
            log.warn(" Sıralı değil: Terminal={}, Son ID={}, Yeni ID={}", terminalId, lastTransactionId, newTransactionId);
            return false;
        }
        lastTransactionMap.put(terminalId, newTransactionId);
        log.info(" Transaction sıralı ve kabul edildi: {} > {}", lastTransactionId, newTransactionId);
        return true;
    }




}
