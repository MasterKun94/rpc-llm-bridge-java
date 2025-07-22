package io.masterkun.ai.registry;

import java.util.Map;

/**
 * Factory interface for creating communication channels in the rpc-llm bridge framework.
 * This interface is responsible for instantiating channel objects that provide
 * communication capabilities between different components of the bridge.
 */
public interface BridgeToolChannelFactory<C extends BridgeToolChannel> {
    /**
     * Creates a new channel instance for communication with the specified target.
     *
     * @param targetAddress The address of the target service or endpoint
     * @param channelOptions Configuration options for the channel (e.g., timeout, credentials)
     * @return A new channel instance
     */
    C create(String targetAddress, Map<String, String> channelOptions);
}
