package com.myapp.backend.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.backend.entity.User;
import com.myapp.backend.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class KafkaConsumerService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public KafkaConsumerService(UserRepository userRepository,
                                ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        System.out.println("\n \u001B[31mKafka Consumer  started\u001B[0m");
    }

    @KafkaListener(topics = "user-events", groupId = "backend-group")
    public void onUserCreated(String message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, String> payload =
                objectMapper.readValue(message, Map.class);

            // Idempotency guard — skip if already saved
            if (userRepository.existsById(payload.get("keycloakId"))) return;

            User user = new User();
            user.setKeycloakId(payload.get("keycloakId"));
            user.setUsername(payload.get("username"));
            user.setEmail(payload.get("email"));
            user.setFirstName(payload.get("firstName"));
            user.setLastName(payload.get("lastName"));
            userRepository.save(user);

            System.out.println("User saved from Kafka: " + user.getUsername());
        } catch (Exception e) {
            System.err.println("Failed to process user event: " + e.getMessage());
        }
    }
}