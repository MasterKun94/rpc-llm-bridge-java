package io.masterkun.ai.grpc;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import io.grpc.CallOptions;
import io.grpc.ManagedChannel;
import io.grpc.MethodDescriptor;
import io.grpc.stub.ClientCalls;
import io.masterkun.ai.tool.BridgeToolCallback;
import io.masterkun.ai.tool.BridgeToolDefinition;
import io.masterkun.ai.tool.BridgeToolMetadata;

/**
 * A callback implementation for gRPC bridge tools. This class handles the execution of gRPC method
 * calls based on tool inputs. It acts as a bridge between the MCP service framework and gRPC
 * services.
 *
 * @param <T> The response message type from the gRPC service
 */
public class GrpcBridgeToolCallback<T extends Message> implements BridgeToolCallback<T> {

    private final GrpcBridgeToolDefinition<T> definition;
    private final BridgeToolMetadata metadata;
    private final ManagedChannel channel;

    /**
     * Constructs a GrpcBridgeToolCallback with the specified gRPC method and channel.
     *
     * @param method  The gRPC method descriptor
     * @param channel The managed channel for communication with the gRPC service
     */
    public GrpcBridgeToolCallback(MethodDescriptor<?, T> method, ManagedChannel channel) {
        this(GrpcBridgeToolDefinition.of(method), new BridgeToolMetadata() {
        }, channel);
    }

    /**
     * Constructs a GrpcBridgeToolCallback with the specified definition, metadata, and channel.
     *
     * @param definition The gRPC bridge tool definition
     * @param metadata   The bridge tool metadata
     * @param channel    The managed channel for communication with the gRPC service
     */
    public GrpcBridgeToolCallback(GrpcBridgeToolDefinition<T> definition,
                                  BridgeToolMetadata metadata, ManagedChannel channel) {
        this.definition = definition;
        this.metadata = metadata;
        this.channel = channel;
    }

    /**
     * Factory method to create a GrpcBridgeToolCallback instance.
     *
     * @param method  The gRPC method descriptor
     * @param channel The managed channel for communication with the gRPC service
     * @param <T>     The response message type
     * @return A new GrpcBridgeToolCallback instance
     */
    public static <T extends Message> GrpcBridgeToolCallback<T> of(MethodDescriptor<?, T> method,
                                                                   ManagedChannel channel) {
        return new GrpcBridgeToolCallback<>(method, channel);
    }

    /**
     * Factory method to create a GrpcBridgeToolCallback instance using a protobuf method
     * descriptor.
     *
     * @param method  The protobuf method descriptor
     * @param channel The managed channel for communication with the gRPC service
     * @return A new GrpcBridgeToolCallback instance
     */
    public static GrpcBridgeToolCallback<? extends Message> of(Descriptors.MethodDescriptor method,
                                                               ManagedChannel channel) {
        return new GrpcBridgeToolCallback<>(GrpcUtils.toGrpcMethod(method), channel);
    }

    /**
     * Returns the tool definition associated with this callback.
     *
     * @return The bridge tool definition
     */
    @Override
    public BridgeToolDefinition<T> getToolDefinition() {
        return definition;
    }

    /**
     * Returns the tool metadata associated with this callback.
     *
     * @return The bridge tool metadata
     */
    @Override
    public BridgeToolMetadata getToolMetadata() {
        return metadata;
    }

    /**
     * Executes the gRPC call with the provided tool input. Converts the JSON input to a protobuf
     * message and makes a blocking unary call to the gRPC service.
     *
     * @param toolInput The JSON input for the gRPC method
     * @return The response from the gRPC service
     */
    @Override
    public T call(String toolInput) {
        @SuppressWarnings("unchecked")
        var method = (MethodDescriptor<DynamicMessage, T>) definition.getMethod();
        Descriptors.Descriptor descriptor = GrpcUtils.getInputDescriptor(method);
        DynamicMessage.Builder builder = DynamicMessage.newBuilder(descriptor);
        ProtoUtils.fromJson(toolInput, builder);
        return ClientCalls.blockingUnaryCall(channel, method, CallOptions.DEFAULT, builder.build());
    }
}
