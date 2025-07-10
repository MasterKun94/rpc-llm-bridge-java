package io.masterkun.ai.grpc;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import io.grpc.CallOptions;
import io.grpc.ClientCall;
import io.grpc.ManagedChannel;
import io.grpc.MethodDescriptor;
import io.grpc.stub.ClientCalls;
import io.masterkun.ai.tool.BridgeToolCallback;
import io.masterkun.ai.tool.BridgeToolDefinition;
import io.masterkun.ai.tool.BridgeToolMetadata;
import io.masterkun.mcp.proto.ExampleServiceGrpc;

public class GrpcBridgeToolCallback<T extends Message> implements BridgeToolCallback<T> {

    private final GrpcBridgeToolDefinition<T> definition;
    private final BridgeToolMetadata metadata;
    private final ManagedChannel channel;

    public GrpcBridgeToolCallback(MethodDescriptor<?, T> method, ManagedChannel channel) {
        this(GrpcBridgeToolDefinition.of(method), new BridgeToolMetadata() {
        }, channel);
    }

    public GrpcBridgeToolCallback(GrpcBridgeToolDefinition<T> definition,
                                  BridgeToolMetadata metadata, ManagedChannel channel) {
        this.definition = definition;
        this.metadata = metadata;
        this.channel = channel;
    }

    public static <T extends Message> GrpcBridgeToolCallback<T> of(MethodDescriptor<?, T> method,
                                                                   ManagedChannel channel) {
        return new GrpcBridgeToolCallback<>(method, channel);
    }

    @Override
    public BridgeToolDefinition<T> getToolDefinition() {
        return definition;
    }

    @Override
    public BridgeToolMetadata getToolMetadata() {
        return metadata;
    }

    @Override
    public T call(String toolInput) {
        @SuppressWarnings("unchecked")
        var method = (MethodDescriptor<DynamicMessage, T>) definition.getMethod();
        ClientCall<?, T> call = channel.newCall(method, CallOptions.DEFAULT);
        ExampleServiceGrpc.ExampleServiceBlockingStub stub =
                ExampleServiceGrpc.newBlockingStub(channel);
        Descriptors.Descriptor descriptor = GrpcUtils.getInputDescriptor(method);
        DynamicMessage.Builder builder = DynamicMessage.newBuilder(descriptor);
        ProtoUtils.fromJson(toolInput, builder);
        return ClientCalls.blockingUnaryCall(channel, method, CallOptions.DEFAULT, builder.build());
    }
}
