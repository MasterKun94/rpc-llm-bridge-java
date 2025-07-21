package io.masterkun.ai.grpc.registry;

import io.masterkun.ai.grpc.GrpcBridgeToolCallback;
import io.masterkun.ai.registry.BridgeTool;

import java.util.List;

public record GrpcBridgeTool(List<String> tags,
                             GrpcBridgeToolCallback<?> toolCallback)
        implements BridgeTool<GrpcBridgeToolCallback<?>> {
}
