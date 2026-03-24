package com.myapp.backend.service;

import com.myapp.backend.dto.RegisterRequest;
import com.myapp.backend.entity.User;
import com.myapp.backend.kafka.KafkaProducerService;
import com.myapp.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final KeycloakService keycloakService;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducer;

    public UserService(KeycloakService keycloakService,
                       UserRepository userRepository,
                       KafkaProducerService kafkaProducer) {
        System.out.println("\n \u001B[31mUser Service started\u001B[0m");
        this.keycloakService = keycloakService;
        this.userRepository = userRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public boolean userExists(String keycloakId) {
        return userRepository.findById(keycloakId).isPresent();
    }

    public boolean userExistsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public void registerUser(RegisterRequest req) {
        String keycloakId = keycloakService.createUser(req);
        System.out.println("\n \u001B[34m" + keycloakId + "\u001B[0m");
        kafkaProducer.sendUserCreatedEvent(keycloakId, req);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> updateUser(String id, User user) {
        return userRepository.findById(id).map(existing -> {
            existing.setUsername(user.getUsername());
            existing.setEmail(user.getEmail());
            existing.setFirstName(user.getFirstName());
            existing.setLastName(user.getLastName());
            return userRepository.save(existing);
        });
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    public boolean existsById(String id) {
        return userRepository.existsById(id);
    }
}