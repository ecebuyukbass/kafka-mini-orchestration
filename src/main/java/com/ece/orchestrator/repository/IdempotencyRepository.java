package com.ece.orchestrator.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import java.time.Duration;


@Repository
@RequiredArgsConstructor
public class IdempotencyRepository {

    private final StringRedisTemplate redisTemplate;
    private static final long EXPIRATION_TIME  = 3600;

    public boolean isDuplicateTransaction(String transactionId){
        Boolean exists = redisTemplate.hasKey(transactionId);
        if (exists !=null && exists){
            return true;
        }

        redisTemplate.opsForValue().set(transactionId,"procossed",Duration.ofSeconds(EXPIRATION_TIME));
        return false;
    }

    public void save(String transactionId) {
        redisTemplate.opsForValue().set(transactionId, "processed", Duration.ofSeconds(EXPIRATION_TIME));
    }

}
