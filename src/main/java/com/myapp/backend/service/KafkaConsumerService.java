package com.myapp.backend.service;

import com.myapp.backend.dto.UserEvent;
import com.myapp.backend.entity.User;
import com.myapp.backend.repository.UserRepository;
import com.myapp.backend.debug.Utility;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaConsumerService {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    public KafkaConsumerService(UserService userService,
                                ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        Utility.kafka("Kafka Consumer started");
    }

    @KafkaListener(topics = "user-events", groupId = "backend-group")
    public void onUserCreated(String message) {

        Utility.kafka("🔥 RAW MESSAGE: " + message);

        try {
            UserEvent payload = objectMapper.readValue(message, UserEvent.class);

            if (userService.existsById(payload.getKeycloakId())) {
                Utility.warn("⚠️ Utente già esistente: " + payload.getUsername());
                return;
            }

            User user = new User();
            user.setKeycloakId(payload.getKeycloakId());
            user.setUsername(payload.getUsername());
            user.setEmail(payload.getEmail());
            user.setFirstName(payload.getFirstName());
            user.setLastName(payload.getLastName());

            userService.saveUser(user); // ✅ QUI

            //Utility.success("💾 Utente salvato da Kafka: " + user.getUsername());

        } catch (Exception e) {
            Utility.error("❌ ERRORE Kafka: " + e.getMessage());
            e.printStackTrace();
        }
    }
}