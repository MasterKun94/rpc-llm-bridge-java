package io.masterkun.ai.grpc.registry;

import io.masterkun.ai.grpc.JSONUtils;
import io.masterkun.ai.registry.BridgeToolChannelFactory;

import java.util.Map;

public class GrpcBridgeToolChannelFactory implements BridgeToolChannelFactory<GrpcBridgeToolChannel> {
    private final GrpcChannelFactory channelFactory = new DefaultGrpcChannelFactory();
    @Override
    public GrpcBridgeToolChannel create(String targetAddress, Map<String, String> channelOptions) {
        GrpcChannelOptions pojo = JSONUtils.OBJECT_MAPPER.convertValue(channelOptions, GrpcChannelOptions.class);
        return new GrpcBridgeToolChannel(channelFactory.create(targetAddress, pojo));
    }
}
