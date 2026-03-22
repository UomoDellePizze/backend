package com.myapp.backend.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private static final String TOPIC = "user-events";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate,
                                 ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper  = objectMapper;
    }

    public void sendUserCreatedEvent(String keycloakId, RegisterRequest req) {
        try {
            Map<String, String> payload = Map.of(
                "keycloakId", keycloakId,
                "username",   req.getUsername(),
                "email",      req.getEmail(),
                "firstName",  req.getFirstName(),
                "lastName",   req.getLastName()
            );
            kafkaTemplate.send(TOPIC, objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize user event", e);
        }
    }
}