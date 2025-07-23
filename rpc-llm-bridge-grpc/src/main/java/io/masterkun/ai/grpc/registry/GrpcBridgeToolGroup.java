package io.masterkun.ai.grpc.registry;

import io.masterkun.ai.registry.BridgeToolGroup;

import java.util.Collections;
import java.util.List;

/**
 * A gRPC implementation of the BridgeToolGroup interface that groups related gRPC tools together.
 */
public record GrpcBridgeToolGroup(String name,
                                  List<GrpcBridgeTool> tools,
                                  GrpcBridgeToolGroupSet toolGroupSet)
        implements BridgeToolGroup<GrpcBridgeTool, GrpcBridgeToolChannel> {

    @Override
    public List<GrpcBridgeTool> tools() {
        return Collections.unmodifiableList(tools);
    }
}
