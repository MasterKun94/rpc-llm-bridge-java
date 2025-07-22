package io.masterkun.ai.registry;

import java.util.Map;

public class BridgeToolChannelHolder<T extends BridgeToolChannel> {
    private final BridgeToolChannelFactory<T> factory;
    private final String targetAddress;
    private final Map<String, String> channelOptions;
    private volatile T channel;

    public BridgeToolChannelHolder(BridgeToolChannelFactory<T> factory,
                                   String targetAddress,
                                   Map<String, String> channelOptions) {
        this.factory = factory;
        this.targetAddress = targetAddress;
        this.channelOptions = channelOptions;
    }

    public T get() {
        if (channel == null) {
            synchronized (this) {
                if (channel == null) {
                    channel = factory.create(targetAddress, channelOptions);
                }
            }
        }
        return channel;
    }
}
