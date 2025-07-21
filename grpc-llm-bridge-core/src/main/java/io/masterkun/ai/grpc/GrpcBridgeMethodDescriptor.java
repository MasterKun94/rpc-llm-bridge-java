package io.masterkun.ai.grpc;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;

/**
 * Interface that provides access to gRPC method metadata. This interface abstracts the details of a
 * gRPC method descriptor, allowing access to method name, service name, input/output types, and
 * options.
 */
public interface GrpcBridgeMethodDescriptor {
    /**
     * Creates a GrpcBridgeMethodDescriptor from a Protobuf MethodDescriptor.
     *
     * @param descriptor The Protobuf method descriptor
     * @return A new GrpcBridgeMethodDescriptor instance
     */
    static GrpcBridgeMethodDescriptor fromDescriptor(Descriptors.MethodDescriptor descriptor) {
        return new GrpcBridgeMethodDescriptor() {
            @Override
            public String getMethodName() {
                return descriptor.getName();
            }

            @Override
            public String getServiceName() {
                return descriptor.getService().getFullName();
            }

            @Override
            public Descriptors.Descriptor getInputType() {
                return descriptor.getInputType();
            }

            @Override
            public Descriptors.Descriptor getOutputType() {
                return descriptor.getOutputType();
            }

            @Override
            public DescriptorProtos.MethodOptions getOptions() {
                return descriptor.getOptions();
            }
        };
    }

    /**
     * Returns the name of the gRPC method.
     *
     * @return The method name
     */
    String getMethodName();

    /**
     * Returns the full name of the service that contains this method.
     *
     * @return The service name
     */
    String getServiceName();

    /**
     * Returns the descriptor for the input message type.
     *
     * @return The input type descriptor
     */
    Descriptors.Descriptor getInputType();

    /**
     * Returns the descriptor for the output message type.
     *
     * @return The output type descriptor
     */
    Descriptors.Descriptor getOutputType();

    /**
     * Returns the method options defined in the proto file.
     *
     * @return The method options
     */
    DescriptorProtos.MethodOptions getOptions();
}
