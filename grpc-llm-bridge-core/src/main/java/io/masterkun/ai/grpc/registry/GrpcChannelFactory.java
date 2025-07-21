package io.masterkun.ai.grpc.registry;

import io.grpc.ManagedChannel;

public interface GrpcChannelFactory {
    ManagedChannel create(String target, GrpcChannelOptions options);
}
