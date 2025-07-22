package io.masterkun.ai.grpc.registry;

import io.masterkun.ai.registry.BridgeToolGroup;

import java.util.List;

/**
 * A gRPC implementation of the BridgeToolGroup interface that groups related gRPC tools together.
 */
public record GrpcBridgeToolGroup(String name, List<GrpcBridgeTool> tools)
        implements BridgeToolGroup<GrpcBridgeTool> {
}
