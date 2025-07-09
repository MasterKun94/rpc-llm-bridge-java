package io.masterkun.ai;

import com.google.protobuf.Descriptors;
import io.grpc.MethodDescriptor;
import io.grpc.ServiceDescriptor;
import io.grpc.protobuf.ProtoMethodDescriptorSupplier;
import io.masterkun.mcp.proto.ExampleService2Grpc;
import io.masterkun.mcp.proto.ExampleServiceGrpc;
import io.masterkun.mcp.proto.McpProto;

public class Main {
    public static void main(String[] args) {

        ServiceDescriptor serviceDescriptor = ExampleService2Grpc.getServiceDescriptor();
        for (MethodDescriptor<?, ?> method : serviceDescriptor.getMethods()) {
            Descriptors.MethodDescriptor methodDescriptor = ((ProtoMethodDescriptorSupplier) method.getSchemaDescriptor()).getMethodDescriptor();
            System.out.println(method.getFullMethodName() + ": " + methodDescriptor.getOptions().getExtension(McpProto.methodDesc));
            Descriptors.Descriptor inputType = methodDescriptor.getInputType();
            System.out.println("Input type:");
            printFieldDescriptor(inputType);
            Descriptors.Descriptor outputType = methodDescriptor.getOutputType();
            System.out.println("Output type:");
            printFieldDescriptor(outputType);
            System.out.println();
        }
    }

    private static void printFieldDescriptor(Descriptors.Descriptor descriptor) {
        for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
            String fieldDesc = field.getOptions().getExtension(McpProto.fieldDesc);
            System.out.println(field.getFullName() + ": " + fieldDesc);
        }
    }
}
