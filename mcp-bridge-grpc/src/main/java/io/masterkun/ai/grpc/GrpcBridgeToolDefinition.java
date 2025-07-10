package io.masterkun.ai.grpc;

import com.google.protobuf.Message;
import io.grpc.MethodDescriptor;
import io.masterkun.ai.tool.BridgeToolDefinition;
import io.masterkun.ai.tool.BridgeToolResultConverterProvider;

public class GrpcBridgeToolDefinition<T extends Message> implements BridgeToolDefinition<T> {
    private final MethodDescriptor<?, T> method;
    private final String name;
    private final String description;

    private GrpcBridgeToolDefinition(MethodDescriptor<?, T> method, String name,
                                     String description) {
        this.method = method;
        this.name = name;
        this.description = description;
    }

    public static <T extends Message> GrpcBridgeToolDefinition<T> of(MethodDescriptor<?, T> method) {
        if (method.getType() != MethodDescriptor.MethodType.UNARY) {
            throw new IllegalArgumentException("Only unary method is supported");
        }
        String name = GrpcUtils.getMethodName(method);
        String description = GrpcUtils.getMethodDesc(method);
        if (description == null) {
            description = StringUtils.toDescStr(name);
        }
        return new GrpcBridgeToolDefinition<>(method, name, description);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String inputSchema() {
        return GrpcUtils.getInputSchema(method);
    }

    @Override
    public BridgeToolResultConverterProvider<T> resultConverter() {
        return GrpcBridgeToolResultConverter::new;
    }

    MethodDescriptor<?, T> getMethod() {
        return method;
    }
}
