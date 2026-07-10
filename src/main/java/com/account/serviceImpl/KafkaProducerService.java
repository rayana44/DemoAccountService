package com.account.serviceImpl;

import com.account.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String TOPIC = "account-data-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendAccountToCardService(Account account) {
        try {
            String accountJson = objectMapper.writeValueAsString(account);
            kafkaTemplate.send(TOPIC, account.getAccountNumber(), accountJson);
            logger.info("Account data sent to Kafka topic: {}", TOPIC);
        } catch (Exception e) {
            logger.error("Error sending account data to Kafka", e);
        }
    }
}
