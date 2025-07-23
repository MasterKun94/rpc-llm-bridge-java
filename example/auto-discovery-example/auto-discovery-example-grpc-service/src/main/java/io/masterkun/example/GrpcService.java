package io.masterkun.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionServiceV1;

/**
 * Main class for the example gRPC service.
 */
public class GrpcService {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(8080)
                .addService(new ExampleService())
                .addService(ProtoReflectionServiceV1.newInstance())
                .build()
                .start();
        System.out.println("Example GRPC server started");
        server.awaitTermination();
    }
}
