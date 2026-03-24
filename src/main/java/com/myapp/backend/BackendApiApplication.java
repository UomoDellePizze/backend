package com.myapp.backend;
import org.springframework.kafka.annotation.EnableKafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableKafka
public class BackendApiApplication {
    public static void main(String[] args) {
        System.out.println("\n \u001B[31mBackend Api Application started\u001B[0m");
        SpringApplication.run(BackendApiApplication.class, args);
    }
}
