package io.masterkun.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the tool callback example caller service. This Spring Boot application
 * serves as the entry point for the service that demonstrates how to use tool callbacks with LLMs.
 */
@SpringBootApplication
public class ExampleToolCallerApplication {

    /**
     * Main method that starts the Spring Boot application.
     */
    public static void main(String[] args) {
        SpringApplication.run(ExampleToolCallerApplication.class, args);
    }
}
