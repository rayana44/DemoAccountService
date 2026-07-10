package com.account.serviceImpl;

import com.account.dto.CreditCardApplicationRequest;
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
    private static final String CREDIT_CARD_TOPIC = "credit-card-application-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendCreditCardApplicationToCardService(Account account, CreditCardApplicationRequest request) {
        try {
            CreditCardApplicationRequest event = new CreditCardApplicationRequest();
            event.setAccountNumber(account.getAccountNumber());
            event.setCustomerName(account.getCustomerName());
            event.setEmail(account.getEmail());
            event.setMobileNumber(account.getMobileNumber());
            event.setCibilScore(account.getCibilScore());
            event.setCardType(request.getCardType());
            event.setRequestReason(request.getRequestReason());
            event.setApplicationDate(java.time.LocalDateTime.now());

            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(CREDIT_CARD_TOPIC, account.getAccountNumber(), eventJson);
            logger.info("Credit card application sent to Kafka topic: {} for account: {}",
                    CREDIT_CARD_TOPIC, account.getAccountNumber());
        } catch (Exception e) {
            logger.error("Error sending credit card application to Kafka", e);
            throw new RuntimeException("Failed to send credit card application: " + e.getMessage());
        }
    }
}