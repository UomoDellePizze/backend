package com.myapp.backend.service;

import com.myapp.backend.dto.UserEvent;
import com.myapp.backend.entity.User;
import com.myapp.backend.repository.UserRepository;
import com.myapp.backend.debug.Utility;
import org.springframework.stereotype.Service;

@Service
public class UserEventService {

    private final UserRepository userRepository;

    public UserEventService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void process(UserEvent event) {

        switch (event.getType()) {

            case "CREATED":
                handleUserCreated(event);
                break;

            case "UPDATED":
                handleUserUpdated(event);
                break;

            case "DELETED":
                handleUserDeleted(event);
                break;

            default:
                Utility.warn("Unknown event type: " + event.getType());
        }
    }

    private void handleUserCreated(UserEvent event) {

        if (userRepository.existsById(event.getKeycloakId())) {
            Utility.warn("User already exists: " + event.getUsername());
            return;
        }

        User user = new User();
        user.setKeycloakId(event.getKeycloakId());
        user.setUsername(event.getUsername());
        user.setEmail(event.getEmail());
        user.setFirstName(event.getFirstName());
        user.setLastName(event.getLastName());

        userRepository.save(user);

        Utility.info("User CREATED: " + user.getUsername());
    }

    private void handleUserUpdated(UserEvent event) {

        userRepository.findById(event.getKeycloakId()).ifPresentOrElse(user -> {

            user.setUsername(event.getUsername());
            user.setEmail(event.getEmail());
            user.setFirstName(event.getFirstName());
            user.setLastName(event.getLastName());

            userRepository.save(user);

            Utility.info("User UPDATED: " + user.getUsername());

        }, () -> Utility.warn("User not found for update: " + event.getKeycloakId()));
    }

    private void handleUserDeleted(UserEvent event) {

        if (!userRepository.existsById(event.getKeycloakId())) {
            Utility.warn("User not found for delete: " + event.getKeycloakId());
            return;
        }

        userRepository.deleteById(event.getKeycloakId());

        Utility.info("User DELETED: " + event.getKeycloakId());
    }
}