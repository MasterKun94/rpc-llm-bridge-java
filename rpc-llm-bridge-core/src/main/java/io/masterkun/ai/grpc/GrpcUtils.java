package io.masterkun.ai.grpc;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoMethodDescriptorSupplier;
import io.masterkun.ai.proto.ToolProto;

import javax.annotation.Nullable;

/**
 * Utility class for working with gRPC method descriptors. Provides methods to extract information
 * from gRPC method descriptors, such as input/output descriptors, method names, descriptions, and
 * schemas.
 */
public class GrpcUtils {

    /**
     * Gets the input message descriptor for a gRPC method.
     *
     * @param method The gRPC method descriptor
     * @return The descriptor for the input message type
     */
    public static Descriptors.Descriptor getInputDescriptor(MethodDescriptor<?, ?> method) {
        GrpcBridgeMethodDescriptor descriptor = getProtoMethod(method);
        return descriptor.getInputType();
    }

    /**
     * Gets the output message descriptor for a gRPC method.
     *
     * @param method The gRPC method descriptor
     * @return The descriptor for the output message type
     */
    public static Descriptors.Descriptor getOutputDescriptor(MethodDescriptor<?, ?> method) {
        GrpcBridgeMethodDescriptor descriptor = getProtoMethod(method);
        return descriptor.getOutputType();
    }

    /**
     * Gets the description of a gRPC method.
     *
     * @param method The gRPC method descriptor
     * @return The method description, or null if not specified
     */
    @Nullable
    public static String getMethodDesc(MethodDescriptor<?, ?> method) {
        GrpcBridgeMethodDescriptor descriptor = getProtoMethod(method);
        return getMethodDesc(descriptor);
    }

    /**
     * Gets the description of a protobuf method. The description is extracted from the method
     * options extension.
     *
     * @param descriptor The method descriptor
     * @return The method description, or null if not specified
     */
    @Nullable
    public static String getMethodDesc(GrpcBridgeMethodDescriptor descriptor) {
        if (descriptor.getOptions().hasExtension(ToolProto.methodDesc)) {
            return descriptor.getOptions().getExtension(ToolProto.methodDesc);
        }
        return null;
    }

    /**
     * Gets the name of a gRPC method.
     *
     * @param method The gRPC method descriptor
     * @return The method name
     */
    public static String getMethodName(MethodDescriptor<?, ?> method) {
        GrpcBridgeMethodDescriptor descriptor = getProtoMethod(method);
        return getMethodName(descriptor);
    }

    /**
     * Gets the name of a protobuf method. The name is extracted from the method options extension
     * if available, otherwise it's constructed from the service name and method name.
     *
     * @param descriptor The method descriptor
     * @return The method name
     */
    public static String getMethodName(GrpcBridgeMethodDescriptor descriptor) {
        if (descriptor.getOptions().hasExtension(ToolProto.methodName)) {
            return descriptor.getOptions().getExtension(ToolProto.methodName);
        }
        return descriptor.getServiceName() + "." + descriptor.getMethodName();
    }

    /**
     * Gets the JSON schema for the input message of a gRPC method.
     *
     * @param method The gRPC method descriptor
     * @return The JSON schema for the input message
     */
    public static String getInputSchema(MethodDescriptor<?, ?> method) {
        GrpcBridgeMethodDescriptor descriptor = getProtoMethod(method);
        return getInputSchema(descriptor);
    }

    /**
     * Gets the JSON schema for the input message of a protobuf method.
     *
     * @param descriptor The method descriptor
     * @return The JSON schema for the input message
     */
    public static String getInputSchema(GrpcBridgeMethodDescriptor descriptor) {
        return ProtoUtils.getJsonSchema(descriptor.getInputType());
    }

    /**
     * Gets the JSON schema for the output message of a gRPC method.
     *
     * @param method The gRPC method descriptor
     * @return The JSON schema for the output message
     */
    public static String getOutputSchema(MethodDescriptor<?, ?> method) {
        GrpcBridgeMethodDescriptor descriptor = getProtoMethod(method);
        return getOutputSchema(descriptor);
    }

    /**
     * Gets the JSON schema for the output message of a protobuf method.
     *
     * @param descriptor The method descriptor
     * @return The JSON schema for the output message
     */
    public static String getOutputSchema(GrpcBridgeMethodDescriptor descriptor) {
        return ProtoUtils.getJsonSchema(descriptor.getOutputType());
    }

    /**
     * Converts a gRPC method descriptor to a protobuf method descriptor.
     *
     * @param method The gRPC method descriptor
     * @return The corresponding protobuf method descriptor
     * @throws IllegalArgumentException if the schema descriptor is not a
     *                                  ProtoMethodDescriptorSupplier
     */
    public static GrpcBridgeMethodDescriptor getProtoMethod(MethodDescriptor<?, ?> method) {
        Object obj = method.getSchemaDescriptor();
        if (!(obj instanceof ProtoMethodDescriptorSupplier)) {
            throw new IllegalArgumentException("Schema descriptor is not a " +
                                               "ProtoMethodDescriptorSupplier");
        }
        Descriptors.MethodDescriptor methodDescriptor =
                ((ProtoMethodDescriptorSupplier) obj).getMethodDescriptor();
        return GrpcBridgeMethodDescriptor.fromDescriptor(methodDescriptor);
    }

    /**
     * Converts a Protobuf method descriptor to a gRPC method descriptor.
     *
     * @param method The Protobuf method descriptor to convert.
     * @return A gRPC {@code MethodDescriptor} with appropriate type, method name, request
     * marshaller, and response marshaller.
     */
    public static MethodDescriptor<? extends Message, ? extends Message> toGrpcMethod(Descriptors.MethodDescriptor method) {
        DynamicMessage defaultInput = DynamicMessage.getDefaultInstance(method.getInputType());
        DynamicMessage defaultOutput = DynamicMessage.getDefaultInstance(method.getOutputType());
        MethodDescriptor.MethodType methodType = method.isClientStreaming() ?
                method.isServerStreaming() ?
                        MethodDescriptor.MethodType.BIDI_STREAMING :
                        MethodDescriptor.MethodType.CLIENT_STREAMING :
                method.isServerStreaming() ?
                        MethodDescriptor.MethodType.SERVER_STREAMING :
                        MethodDescriptor.MethodType.UNARY;
        return MethodDescriptor.<Message, Message>newBuilder()
                .setType(methodType)
                .setFullMethodName(MethodDescriptor.generateFullMethodName(
                        method.getService().getFullName(), method.getName()))
                .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(defaultInput))
                .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(defaultOutput))
                .setSchemaDescriptor(new ProtoMethodDescriptorSupplier() {

                    @Override
                    public Descriptors.FileDescriptor getFileDescriptor() {
                        return method.getFile();
                    }

                    @Override
                    public Descriptors.ServiceDescriptor getServiceDescriptor() {
                        return method.getService();
                    }

                    @Override
                    public Descriptors.MethodDescriptor getMethodDescriptor() {
                        return method;
                    }
                })
                .build();
    }
}
