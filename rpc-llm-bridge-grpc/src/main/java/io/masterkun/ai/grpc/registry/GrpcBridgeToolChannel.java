package io.masterkun.ai.grpc.registry;

import io.grpc.ManagedChannel;
import io.masterkun.ai.registry.BridgeToolChannel;

public record GrpcBridgeToolChannel(ManagedChannel channel) implements BridgeToolChannel {

    @Override
    public void close() {
        channel.shutdown();
    }
}
