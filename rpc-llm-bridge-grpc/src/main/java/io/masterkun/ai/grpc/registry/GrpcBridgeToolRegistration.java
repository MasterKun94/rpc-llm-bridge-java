package io.masterkun.ai.grpc.registry;

import io.masterkun.ai.registry.BridgeToolChannelHolder;
import io.masterkun.ai.registry.BridgeToolRegistration;

import java.util.Collections;
import java.util.Map;

public class GrpcBridgeToolRegistration implements BridgeToolRegistration<GrpcBridgeToolGroupSet, GrpcBridgeToolChannel, GrpcBridgeTool> {
    private final String name;
    private final String targetAddress;
    private final Map<String, String> options;
    private final BridgeToolChannelHolder<GrpcBridgeToolChannel> channelHolder;
    private final GrpcBridgeToolGroupSet groupSet;

    public GrpcBridgeToolRegistration(String targetAddress) {
        this(targetAddress, targetAddress, Collections.emptyMap());
    }

    public GrpcBridgeToolRegistration(String name, String targetAddress) {
        this(name, targetAddress, Collections.emptyMap());
    }

    public GrpcBridgeToolRegistration(String name, String targetAddress, Map<String, String> options) {
        this.name = name;
        this.targetAddress = targetAddress;
        this.options = Collections.unmodifiableMap(options);
        this.channelHolder = new BridgeToolChannelHolder<>(new GrpcBridgeToolChannelFactory(), targetAddress, options);
        this.groupSet = new GrpcBridgeToolGroupSet(this);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String targetAddress() {
        return targetAddress;
    }

    @Override
    public Map<String, String> options() {
        return options;
    }

    @Override
    public GrpcBridgeToolGroupSet groupSet() {
        return groupSet;
    }

    @Override
    public BridgeToolChannelHolder<GrpcBridgeToolChannel> getChannelHolder() {
        return channelHolder;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GrpcBridgeToolRegistration[name=");
        builder.append(name);
        builder.append(", targetAddress=");
        builder.append(targetAddress);
        builder.append(", groupSet=[");
        boolean first = true;
        for (GrpcBridgeToolGroup group : groupSet.getGroups()) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append(group.name());
        }
        builder.append("]]");
        return builder.toString();
    }
}
