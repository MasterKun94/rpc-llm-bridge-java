package io.masterkun.ai.grpc.registry;

import com.google.protobuf.Descriptors;
import io.grpc.ManagedChannel;
import io.masterkun.ai.grpc.GrpcBridgeToolCallback;
import io.masterkun.ai.registry.BridgeTool;

import java.util.List;

public record GrpcBridgeTool(List<String> tags, Descriptors.MethodDescriptor methodDescriptor)
        implements BridgeTool<GrpcBridgeToolCallback<?>> {

    @Override
    public GrpcBridgeToolCallback<?> createToolCallback(Object serviceChannel) {
        return GrpcBridgeToolCallback.of(methodDescriptor, (ManagedChannel) serviceChannel);
    }
}
