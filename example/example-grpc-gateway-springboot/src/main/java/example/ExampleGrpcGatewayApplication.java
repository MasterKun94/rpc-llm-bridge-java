package example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.masterkun.ai.grpc.GrpcBridgeToolCallback;
import io.masterkun.ai.tool.BridgeToolCallback;
import io.masterkun.mcp.proto.ExampleServiceGrpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ExampleGrpcGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleGrpcGatewayApplication.class, args);
    }

    @Bean
    public ManagedChannel grpcChannel() {
        return ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();
    }

    @Bean
    public BridgeToolCallback<?> getTime(ManagedChannel channel) {
        return new GrpcBridgeToolCallback<>(ExampleServiceGrpc.getGetTimeMethod(), channel);
    }

    @Bean
    public BridgeToolCallback<?> toUpperCache(ManagedChannel channel) {
        return new GrpcBridgeToolCallback<>(ExampleServiceGrpc.getToUpperCaseMethod(), channel);
    }
}
