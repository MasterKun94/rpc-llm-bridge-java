package example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.masterkun.ai.grpc.GrpcBridgeToolCallback;
import io.masterkun.ai.tool.BridgeToolCallback;
import io.masterkun.tool.proto.ExampleServiceGrpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * MCP Gateway application that exposes gRPC services as MCP tools.
 * This application connects to the example gRPC service and registers
 * its methods as MCP tools that can be called by AI models.
 */
@SpringBootApplication
public class ExampleGrpcBridgeApplication {

    /**
     * Entry point for the MCP Gateway application.
     */
    public static void main(String[] args) {
        SpringApplication.run(ExampleGrpcBridgeApplication.class, args);
    }

    /**
     * Creates a gRPC channel to connect to the example gRPC service.
     */
    @Bean
    public ManagedChannel grpcChannel() {
        return ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();
    }

    /**
     * Registers the getTime method as an MCP tool.
     */
    @Bean
    public BridgeToolCallback<?> getTime(ManagedChannel channel) {
        return new GrpcBridgeToolCallback<>(ExampleServiceGrpc.getGetTimeMethod(), channel);
    }

    /**
     * Registers the toUpperCase method as an MCP tool.
     */
    @Bean
    public BridgeToolCallback<?> toUpperCache(ManagedChannel channel) {
        return new GrpcBridgeToolCallback<>(ExampleServiceGrpc.getToUpperCaseMethod(), channel);
    }
}
