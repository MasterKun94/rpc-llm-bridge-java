package io.masterkun.ai.grpc;

import com.google.protobuf.Message;
import io.masterkun.ai.tool.BridgeToolResultConverter;

/**
 * A converter for transforming gRPC response messages to string representations.
 * This class is responsible for converting protobuf Message objects to formatted strings
 * that can be returned as tool results in the MCP framework.
 *
 * @param <T> The type of protobuf Message to convert
 */
public class GrpcBridgeToolResultConverter<T extends Message> implements BridgeToolResultConverter<T> {

    /**
     * Converts a protobuf Message to a formatted string representation.
     *
     * @param result The protobuf Message to convert
     * @return A string representation of the message
     */
    @Override
    public String convert(T result) {
        return ProtoUtils.formatString(result);
    }
}
