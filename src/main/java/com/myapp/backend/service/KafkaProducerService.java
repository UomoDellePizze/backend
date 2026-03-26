package com.myapp.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.backend.dto.RegisterRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class KafkaProducerService {

    private static final String TOPIC = "user-events";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper) {
        System.out.println("\n \u001B[31mKafka Producer  started\u001B[0m");
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendUserCreatedEvent(String keycloakId, RegisterRequest req) {
        try {
            Map<String, String> payload = Map.of(
                "keycloakId", keycloakId,
                "username",   req.getUsername(),
                "email",      req.getEmail(),
                "firstName",  req.getFirstName() != null ? req.getFirstName() : "",
                "lastName",   req.getLastName()  != null ? req.getLastName()  : ""
            );
            kafkaTemplate.send(TOPIC, objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize user event", e);
        }
    }
}