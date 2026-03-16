package com.myapp.backend;
import org.springframework.kafka.annotation.EnableKafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableKafka
public class BackendApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApiApplication.class, args);
    }
}
