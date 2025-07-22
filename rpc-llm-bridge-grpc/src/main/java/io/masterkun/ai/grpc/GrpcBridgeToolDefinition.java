package io.masterkun.ai.grpc;

import com.google.protobuf.Message;
import io.grpc.MethodDescriptor;
import io.masterkun.ai.tool.BridgeToolDefinition;
import io.masterkun.ai.tool.BridgeToolResultConverterProvider;

/**
 * A definition class for gRPC bridge tools. This class provides metadata about a gRPC method that
 * can be exposed as an MCP tool, including its name, description, input schema, and result
 * conversion capabilities.
 *
 * @param <T> The response message type from the gRPC service
 */
public class GrpcBridgeToolDefinition<T extends Message> implements BridgeToolDefinition<T> {
    private final MethodDescriptor<?, T> method;
    private final String name;
    private final String description;

    /**
     * Private constructor for creating a GrpcBridgeToolDefinition.
     *
     * @param method      The gRPC method descriptor
     * @param name        The name of the tool
     * @param description The description of the tool
     */
    private GrpcBridgeToolDefinition(MethodDescriptor<?, T> method, String name,
                                     String description) {
        this.method = method;
        this.name = name;
        this.description = description;
    }

    /**
     * Factory method to create a GrpcBridgeToolDefinition from a gRPC method descriptor. Only unary
     * methods are supported. The method name and description are extracted from the method
     * descriptor or generated if not available.
     *
     * @param method The gRPC method descriptor
     * @param <T>    The response message type
     * @return A new GrpcBridgeToolDefinition instance
     * @throws IllegalArgumentException if the method is not a unary method
     */
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

    /**
     * Returns the name of the tool.
     *
     * @return The tool name
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * Returns the description of the tool.
     *
     * @return The tool description
     */
    @Override
    public String description() {
        return description;
    }

    /**
     * Returns the JSON schema for the input of the tool. This schema is derived from the input
     * message type of the gRPC method.
     *
     * @return The JSON schema for the tool input
     */
    @Override
    public String inputSchema() {
        return GrpcUtils.getInputSchema(method);
    }

    /**
     * Returns a provider for creating result converters for this tool. The result converter
     * transforms the gRPC response message to the desired output format.
     *
     * @return A provider for creating result converters
     */
    @Override
    public BridgeToolResultConverterProvider<T> resultConverter() {
        return GrpcBridgeToolResultConverter::new;
    }

    /**
     * Returns the gRPC method descriptor associated with this tool definition.
     *
     * @return The gRPC method descriptor
     */
    MethodDescriptor<?, T> getMethod() {
        return method;
    }
}
