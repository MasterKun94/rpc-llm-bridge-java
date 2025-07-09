package io.masterkun.ai;

import com.google.protobuf.Descriptors;
import io.grpc.MethodDescriptor;
import io.grpc.ServiceDescriptor;
import io.grpc.protobuf.ProtoMethodDescriptorSupplier;
import io.masterkun.mcp.proto.ExampleProto;
import io.masterkun.mcp.proto.ExampleServiceGrpc;
import io.masterkun.mcp.proto.McpProto;

public class Main {
    public static void main(String[] args) {
        // Test the proto2json method
        testProto2Json();

        // Test the getJsonSchema method
        testGetJsonSchema();

        // Print service descriptor information
        printServiceDescriptorInfo();
    }

    private static void testProto2Json() {
        System.out.println("Testing ProtoUtils.proto2json method:");

        // Create a ToUpperCaseReq message
        ExampleProto.ToUpperCaseReq request = ExampleProto.ToUpperCaseReq.newBuilder()
                .setMessage("Hello, World!")
                .build();

        // Convert the message to JSON
        String json = ProtoUtils.proto2json(request);

        // Print the JSON
        System.out.println("Message: " + request);
        System.out.println("JSON: " + json);

        // Test with null input
        String nullJson = ProtoUtils.proto2json(null);
        System.out.println("Null message JSON: " + nullJson);

        System.out.println();
    }

    private static void testGetJsonSchema() {
        System.out.println("Testing ProtoUtils.getJsonSchema method:");

        // Create a ToUpperCaseReq message
        ExampleProto.ToUpperCaseReq request = ExampleProto.ToUpperCaseReq.newBuilder()
                .setMessage("Hello, World!")
                .build();

        // Generate JSON schema for the message
        String schema = ProtoUtils.getJsonSchema(request);

        // Print the schema
        System.out.println("Message: " + request);
        System.out.println("JSON Schema: " + schema);

        // Test with a more complex message
        ExampleProto.ZonedTimeReq timeRequest = ExampleProto.ZonedTimeReq.newBuilder()
                .setTimezone("UTC+8")
                .build();

        // Generate JSON schema for the complex message
        String timeSchema = ProtoUtils.getJsonSchema(timeRequest);

        // Print the schema
        System.out.println("\nMessage: " + timeRequest);
        System.out.println("JSON Schema: " + timeSchema);

        // Test with null input
        String nullSchema = ProtoUtils.getJsonSchema(null);
        System.out.println("\nNull message JSON Schema: " + nullSchema);

        System.out.println();
    }

    private static void printServiceDescriptorInfo() {
        ServiceDescriptor serviceDescriptor = ExampleServiceGrpc.getServiceDescriptor();
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
