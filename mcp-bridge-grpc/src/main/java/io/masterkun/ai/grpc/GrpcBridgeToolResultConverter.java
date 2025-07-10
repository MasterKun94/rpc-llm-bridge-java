package io.masterkun.ai.grpc;

import com.google.protobuf.Message;
import io.masterkun.ai.tool.BridgeToolResultConverter;

public class GrpcBridgeToolResultConverter<T extends Message> implements BridgeToolResultConverter<T> {
    @Override
    public String convert(T result) {
        return ProtoUtils.formatString(result);
    }
}
