package io.masterkun.ai.grpc;

import com.google.protobuf.Descriptors;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoMethodDescriptorSupplier;
import io.masterkun.mcp.proto.McpProto;

import javax.annotation.Nullable;

public class GrpcUtils {

    public static Descriptors.Descriptor getInputDescriptor(MethodDescriptor<?, ?> method) {
        Descriptors.MethodDescriptor descriptor = extractProtoMethod(method);
        return descriptor.getInputType();
    }

    public static Descriptors.Descriptor getOutputDescriptor(MethodDescriptor<?, ?> method) {
        Descriptors.MethodDescriptor descriptor = extractProtoMethod(method);
        return descriptor.getOutputType();
    }

    @Nullable
    public static String getMethodDesc(MethodDescriptor<?, ?> method) {
        Descriptors.MethodDescriptor descriptor = extractProtoMethod(method);
        return getMethodDesc(descriptor);
    }

    @Nullable
    public static String getMethodDesc(Descriptors.MethodDescriptor descriptor) {
        if (descriptor.getOptions().hasExtension(McpProto.methodDesc)) {
            return descriptor.getOptions().getExtension(McpProto.methodDesc);
        }
        return null;
    }

    public static String getMethodName(MethodDescriptor<?, ?> method) {
        Descriptors.MethodDescriptor descriptor = extractProtoMethod(method);
        return getMethodName(descriptor);
    }

    public static String getMethodName(Descriptors.MethodDescriptor descriptor) {
        if (descriptor.getOptions().hasExtension(McpProto.methodName)) {
            return descriptor.getOptions().getExtension(McpProto.methodName);
        }
        return descriptor.getService().getName() + "." + descriptor.getName();
    }

    public static String getInputSchema(MethodDescriptor<?, ?> method) {
        Descriptors.MethodDescriptor descriptor = extractProtoMethod(method);
        return getInputSchema(descriptor);
    }

    public static String getInputSchema(Descriptors.MethodDescriptor descriptor) {
        return ProtoUtils.getJsonSchema(descriptor.getInputType());
    }

    public static String getOutputSchema(MethodDescriptor<?, ?> method) {
        Descriptors.MethodDescriptor descriptor = extractProtoMethod(method);
        return getOutputSchema(descriptor);
    }

    public static String getOutputSchema(Descriptors.MethodDescriptor descriptor) {
        return ProtoUtils.getJsonSchema(descriptor.getOutputType());
    }

    private static Descriptors.MethodDescriptor extractProtoMethod(MethodDescriptor<?, ?> method) {
        Object obj = method.getSchemaDescriptor();
        if (!(obj instanceof ProtoMethodDescriptorSupplier)) {
            throw new IllegalArgumentException("Schema descriptor is not a ProtoMethodDescriptorSupplier");
        }
        return ((ProtoMethodDescriptorSupplier) obj).getMethodDescriptor();
    }
}
