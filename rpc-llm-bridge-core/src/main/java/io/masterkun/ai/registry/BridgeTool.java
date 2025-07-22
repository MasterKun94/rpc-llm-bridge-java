package io.masterkun.ai.registry;

import io.masterkun.ai.tool.BridgeToolCallback;

import java.util.List;

/**
 * Interface representing a tool in the rpc-llm bridge framework. A tool is a callable unit of functionality
 * that can be discovered and invoked through the bridge. Each tool is associated with tags for categorization
 * and can create tool callbacks for execution.
 */
public interface BridgeTool<T extends BridgeToolCallback<?>, C extends BridgeToolChannel> {

    /**
     * Returns the list of tags associated with this tool. Tags are used for categorization
     * and discovery of tools.
     *
     * @return A list of string tags
     */
    List<String> tags();

    /**
     * Creates a tool callback instance for this tool. The callback is the executable
     * representation of the tool that can be invoked by clients.
     *
     * @param channelHolder The channel holder providing communication capabilities
     * @return A new tool callback instance
     */
    T createToolCallback(BridgeToolChannelHolder<C> channelHolder);
}
