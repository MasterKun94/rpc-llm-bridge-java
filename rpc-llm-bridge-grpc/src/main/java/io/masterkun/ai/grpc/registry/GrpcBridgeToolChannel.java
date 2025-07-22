package io.masterkun.ai.grpc.registry;

import io.grpc.ManagedChannel;
import io.masterkun.ai.registry.BridgeToolChannel;

/**
 * A gRPC implementation of the BridgeToolChannel interface that wraps a gRPC ManagedChannel.
 */
public record GrpcBridgeToolChannel(ManagedChannel channel) implements BridgeToolChannel {

    /**
     * Closes this channel by shutting down the underlying gRPC channel.
     */
    @Override
    public void close() {
        channel.shutdown();
    }
}
