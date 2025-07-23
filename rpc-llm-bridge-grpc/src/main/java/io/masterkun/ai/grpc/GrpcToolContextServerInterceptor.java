package io.masterkun.ai.grpc;

import com.fasterxml.jackson.core.type.TypeReference;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.masterkun.ai.tool.BridgeToolContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Server interceptor that extracts tool context from incoming gRPC call metadata
 * and makes it available through the GrpcBridgeToolContext.
 */
public class GrpcToolContextServerInterceptor implements ServerInterceptor {

    /**
     * Intercepts incoming gRPC calls to extract tool context from the call metadata.
     *
     * @param call The server call
     * @param headers The call headers containing metadata
     * @param next The next handler in the interceptor chain
     * @return A listener for the server call
     */
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        // Extract tool context from metadata if present
        String contextJson = headers.get(GrpcBridgeToolContext.METADATA_KEY);

        if (contextJson != null && !contextJson.isEmpty()) {
            try {
                // Deserialize the JSON context
                Map<String, Object> contextMap = JSONUtils.fromJson(contextJson,
                        new TypeReference<HashMap<String, Object>>() {});

                // Create a new BridgeToolContext with the deserialized map
                BridgeToolContext toolContext = new BridgeToolContext(contextMap);

                // Store the context in the gRPC Context
                Context context = Context.current().withValue(GrpcBridgeToolContext.CONTEXT_KEY, toolContext);

                // Continue the call with the updated context
                return Contexts.interceptCall(context, call, headers, next);
            } catch (Exception e) {
                // Log error but continue with empty context
                System.err.println("Error parsing tool context: " + e.getMessage());
            }
        }

        // If no context was found or an error occurred, continue with the original context
        return next.startCall(call, headers);
    }
}
