package io.masterkun.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.masterkun.ai.grpc.GrpcBridgeToolCallback;
import io.masterkun.ai.tool.BridgeToolCallback;
import io.masterkun.toolcall.proto.ExampleServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up gRPC channel and tool callbacks. Defines beans for connecting
 * to the gRPC service and creating tool callbacks for the example service methods.
 */
@Configuration
public class ExampleToolConfig {

    /**
     * Creates a gRPC managed channel for connecting to the example service.
     */
    @Bean
    public ManagedChannel grpcChannel() {
        return ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();
    }

    /**
     * Creates a tool callback for the getTime method of the example service.
     */
    @Bean
    public BridgeToolCallback<?> getTime(ManagedChannel channel) {
        return new GrpcBridgeToolCallback<>(ExampleServiceGrpc.getGetTimeMethod(), channel);
    }

    /**
     * Creates a tool callback for the toUpperCase method of the example service.
     */
    @Bean
    public BridgeToolCallback<?> toUpperCache(ManagedChannel channel) {
        return new GrpcBridgeToolCallback<>(ExampleServiceGrpc.getToUpperCaseMethod(), channel);
    }
}
