package com.myapp.backend.service;

import com.myapp.backend.dto.RegisterRequest;
import com.myapp.backend.entity.User;
import com.myapp.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final KeycloakService keycloakService;
    private final UserRepository userRepository;

    public UserService(KeycloakService keycloakService,
                       UserRepository userRepository) {
        this.keycloakService = keycloakService;
        this.userRepository = userRepository;
    }

    public void registerUser(RegisterRequest req) {

        // crea utente su keycloak
        String keycloakId = keycloakService.createUser(req);

        // salva nel database locale
        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        userRepository.save(user);
    }
}