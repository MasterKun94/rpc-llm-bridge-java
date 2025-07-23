package io.masterkun.ai.registry;

import java.util.Map;

/**
 * A holder class for lazily creating and caching communication channels in the rpc-llm bridge framework.
 * This class implements the lazy initialization pattern with double-checked locking to ensure
 * that channels are created only when needed and only once, even in multi-threaded environments.
 */
public class BridgeToolChannelHolder<C extends BridgeToolChannel> implements AutoCloseable {
    /** The factory used to create channel instances */
    private final BridgeToolChannelFactory<C> factory;

    /** The address of the target service or endpoint */
    private final String targetAddress;

    /** Configuration options for the channel */
    private final Map<String, String> channelOptions;

    /** The lazily initialized channel instance */
    private volatile C channel;

    /**
     * Creates a new channel holder with the specified factory and connection parameters.
     *
     * @param factory The factory to use for creating channel instances
     * @param targetAddress The address of the target service or endpoint
     * @param channelOptions Configuration options for the channel
     */
    public BridgeToolChannelHolder(BridgeToolChannelFactory<C> factory,
                                   String targetAddress,
                                   Map<String, String> channelOptions) {
        this.factory = factory;
        this.targetAddress = targetAddress;
        this.channelOptions = channelOptions;
    }

    /**
     * Gets the channel instance, creating it if it doesn't already exist.
     * This method uses double-checked locking to ensure thread safety.
     *
     * @return The channel instance
     */
    public C get() {
        if (channel == null) {
            synchronized (this) {
                if (channel == null) {
                    channel = factory.create(targetAddress, channelOptions);
                }
            }
        }
        return channel;
    }

    @Override
    public void close() {
        if (channel != null) {
            channel.close();
        }
    }
}
