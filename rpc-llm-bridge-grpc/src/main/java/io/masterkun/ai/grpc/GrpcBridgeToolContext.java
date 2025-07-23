package io.masterkun.ai.grpc;

import io.grpc.Context;
import io.grpc.Metadata;
import io.masterkun.ai.tool.BridgeToolContext;

public class GrpcBridgeToolContext {
    static final Metadata.Key<String> METADATA_KEY =
            Metadata.Key.of("tool-context", Metadata.ASCII_STRING_MARSHALLER);
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
}
