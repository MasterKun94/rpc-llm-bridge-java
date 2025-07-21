package io.masterkun.ai.grpc.registry;

import io.masterkun.ai.registry.BridgeToolGroup;

import java.util.List;

public record GrpcBridgeToolGroup(String name, List<GrpcBridgeTool> tools)
        implements BridgeToolGroup<GrpcBridgeTool> {
}
