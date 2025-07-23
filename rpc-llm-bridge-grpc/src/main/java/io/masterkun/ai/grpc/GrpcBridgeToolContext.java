package io.masterkun.ai.grpc;

import com.fasterxml.jackson.core.type.TypeReference;
import io.grpc.Context;
import io.grpc.Metadata;
import io.masterkun.ai.tool.BridgeToolContext;

public class GrpcBridgeToolContext {
    static final Metadata.Key<BridgeToolContext> METADATA_KEY =
            Metadata.Key.of("tool-context-bin", new ToolContextMarshaller());
    static final Context.Key<BridgeToolContext> CONTEXT_KEY = Context.key("tool-context");

    /**
     * Retrieves the current tool context from the gRPC Context.
     * If no context is set, returns an empty context.
     *
     * @return The current BridgeToolContext or EMPTY if none exists
     */
    public static BridgeToolContext current() {
        BridgeToolContext context = CONTEXT_KEY.get();
        return context != null ? context : BridgeToolContext.EMPTY;
    }

    private static class ToolContextMarshaller implements Metadata.BinaryMarshaller<BridgeToolContext> {
        @Override
        public byte[] toBytes(BridgeToolContext value) {
            return JSONUtils.toJsonBytes(value.getContext());
        }
        @Override
        public BridgeToolContext parseBytes(byte[] serialized) {
            return new BridgeToolContext(JSONUtils.fromJson(serialized, new TypeReference<>() {
            }));
        }
    }
}
