package io.masterkun.ai.grpc.registry;

import com.google.protobuf.Descriptors;
import io.masterkun.ai.grpc.GrpcBridgeToolCallback;
import io.masterkun.ai.registry.BridgeTool;
import io.masterkun.ai.registry.BridgeToolChannelHolder;
import io.masterkun.ai.registry.BridgeToolGroup;
import org.checkerframework.checker.units.qual.C;

import java.util.List;
import java.util.Set;

/**
 * A gRPC implementation of the BridgeTool interface that represents a callable gRPC method.
 */
public record GrpcBridgeTool(Set<String> tags,
                             Descriptors.MethodDescriptor methodDescriptor,
                             GrpcBridgeToolGroup toolGroup)
        implements BridgeTool<GrpcBridgeToolCallback<?>, GrpcBridgeToolChannel> {

    /**
     * Creates a callback for this tool using the provided channel holder.
     *
     * @param channelHolder The holder providing the gRPC channel for communication
     * @return A callback that can execute the gRPC method
     */
    @Override
    public GrpcBridgeToolCallback<?> createToolCallback(BridgeToolChannelHolder<GrpcBridgeToolChannel> channelHolder) {
        return GrpcBridgeToolCallback.of(methodDescriptor, channelHolder.get().channel());
    }
}
