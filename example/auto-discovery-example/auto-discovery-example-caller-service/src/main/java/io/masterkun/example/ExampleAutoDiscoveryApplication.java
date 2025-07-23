package io.masterkun.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 自动工具发现示例的主应用类，启动Spring Boot应用。
 */
@SpringBootApplication
public class ExampleAutoDiscoveryApplication {

    /**
     * 应用程序入口点，启动Spring Boot应用。
     */
    public static void main(String[] args) {
        SpringApplication.run(ExampleAutoDiscoveryApplication.class, args);
    }
}
