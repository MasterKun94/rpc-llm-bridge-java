package io.masterkun.ai.grpc.registry;

import com.google.protobuf.Descriptors;
import io.masterkun.ai.grpc.GrpcBridgeToolCallback;
import io.masterkun.ai.registry.BridgeTool;
import io.masterkun.ai.registry.BridgeToolChannelHolder;

import java.util.List;

public record GrpcBridgeTool(List<String> tags, Descriptors.MethodDescriptor methodDescriptor)
        implements BridgeTool<GrpcBridgeToolCallback<?>, GrpcBridgeToolChannel> {

    @Override
    public GrpcBridgeToolCallback<?> createToolCallback(BridgeToolChannelHolder<GrpcBridgeToolChannel> channelHolder) {
        return GrpcBridgeToolCallback.of(methodDescriptor, channelHolder.get().channel());
    }
}
