package io.masterkun.ai.grpc;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.masterkun.ai.tool.BridgeToolContext;

public class GrpcToolContextClientInterceptor implements ClientInterceptor {
    private final BridgeToolContext toolContext;

    public GrpcToolContextClientInterceptor(BridgeToolContext toolContext) {
        this.toolContext = toolContext;
    }

    /**
     * Intercepts outgoing gRPC calls to add tool context to the call metadata.
     *
     * @param method The gRPC method being called
     * @param callOptions The call options
     * @param next The next channel in the interceptor chain
     * @return A ClientCall that includes the tool context in its metadata
     */
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
                                                               CallOptions callOptions, Channel next) {
        ClientCall<ReqT, RespT> call = next.newCall(method, callOptions);

        return new ForwardingClientCall.SimpleForwardingClientCall<>(call) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                if (toolContext != null && toolContext != BridgeToolContext.EMPTY) {
                    // Serialize the tool context and add it to the metadata
                    headers.put(GrpcBridgeToolContext.METADATA_KEY, toolContext);
                }
                super.start(responseListener, headers);
            }
        };
    }
}
