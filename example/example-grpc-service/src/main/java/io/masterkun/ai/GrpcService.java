package io.masterkun.ai;

import io.grpc.Server;
import io.grpc.ServerBuilder;

/**
 * Main class for the example gRPC service.
 */
public class GrpcService {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(8080)
                .addService(new ExampleService())
                .build()
                .start();
        System.out.println("Example GRPC server started");
        server.awaitTermination();
    }
}
