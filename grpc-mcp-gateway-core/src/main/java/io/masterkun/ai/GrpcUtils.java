package io.masterkun.ai;

import com.google.protobuf.Descriptors;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoMethodDescriptorSupplier;
import io.masterkun.mcp.proto.McpProto;

import javax.annotation.Nullable;

public class GrpcUtils {

    @Nullable
    public static String getMethodDesc(MethodDescriptor<?, ?> method) {
        Descriptors.MethodDescriptor descriptor = extractProtoMethod(method);
        if (descriptor.getOptions().hasExtension(McpProto.methodDesc)) {
            return descriptor.getOptions().getExtension(McpProto.methodDesc);
        }
        return null;
    }

    public static String getMethodName(MethodDescriptor<?, ?> method) {
        Descriptors.MethodDescriptor descriptor = extractProtoMethod(method);
        if (descriptor.getOptions().hasExtension(McpProto.methodName)) {
            return descriptor.getOptions().getExtension(McpProto.methodName);
        }
        return method.getFullMethodName();
    }

    public static String getInputSchema(MethodDescriptor<?, ?> method) {
        Descriptors.MethodDescriptor descriptor = extractProtoMethod(method);
        return ProtoUtils.getJsonSchema(descriptor.getInputType());
    }

    public static String getOutputSchema(MethodDescriptor<?, ?> method) {
        Descriptors.MethodDescriptor descriptor = extractProtoMethod(method);
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
