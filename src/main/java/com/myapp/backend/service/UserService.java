package com.myapp.backend.service;

import com.myapp.backend.dto.RegisterRequest;
import com.myapp.backend.entity.User;
import com.myapp.backend.repository.UserRepository;
import com.myapp.backend.kafka.KafkaProducerService;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;
@Service
public class UserService {

    private final KeycloakService keycloakService;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducer;
    public UserService(KeycloakService keycloakService,UserRepository userRepository,KafkaProducerService kafkaProducer) {
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

        // crea utente su keycloak
        String keycloakId = keycloakService.createUser(req);
        System.out.println(keycloakId);
        // salva nel database locale
        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        saveUser(user);
        System.out.println("Utente registrato con successo: " + user);
        kafkaProducer.sendUserCreatedEvent(user.getUsername());
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