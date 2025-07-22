package io.masterkun.ai.registry;

import io.masterkun.ai.tool.BridgeToolCallback;

import java.util.List;

/**
 * Interface for discovering tool callbacks in the rpc-llm bridge framework. This interface
 * provides methods to find tool callbacks based on group names and tags, enabling clients
 * to locate and use available tools in the system.
 * <p>
 * The discovery mechanism is a key component of the service discovery functionality,
 * allowing dynamic lookup of tools without requiring direct knowledge of their location.
 */
public interface BridgeToolDiscovery {
    /**
     * Finds all tool callbacks belonging to the specified group.
     *
     * @param groupName The name of the group to search for
     * @return A list of tool callbacks in the specified group
     */
    List<BridgeToolCallback<?>> findToolCallbacks(String groupName);

    /**
     * Finds all tool callbacks that match the specified tags.
     *
     * @param tags A list of tags to match against tool callbacks
     * @return A list of tool callbacks that match the specified tags
     */
    List<BridgeToolCallback<?>> findToolCallbacks(List<String> tags);

    /**
     * Finds all tool callbacks that belong to the specified group and match the specified tags.
     *
     * @param groupName The name of the group to search for
     * @param tags A list of tags to match against tool callbacks
     * @return A list of tool callbacks that belong to the specified group and match the specified tags
     */
    List<BridgeToolCallback<?>> findToolCallbacks(String groupName, List<String> tags);
}
