package io.masterkun.ai.grpc.registry;

import io.grpc.ManagedChannel;

/**
 * Factory interface for creating gRPC channels with specified options.
 */
public interface GrpcChannelFactory {
    /**
     * Creates a new gRPC channel for the specified target with the given options.
     *
     * @param target  The target service address
     * @param options Configuration options for the channel
     * @return A configured gRPC managed channel
     */
    ManagedChannel create(String target, GrpcChannelOptions options);
}
