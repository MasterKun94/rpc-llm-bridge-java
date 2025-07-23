package io.masterkun.ai.registry;

import io.masterkun.ai.tool.BridgeToolCallback;

import java.util.List;
import java.util.Set;

/**
 * Interface representing a tool in the rpc-llm bridge framework. A tool is a callable unit of functionality
 * that can be discovered and invoked through the bridge. Each tool is associated with tags for categorization
 * and can create tool callbacks for execution.
 */
public interface BridgeTool<TC extends BridgeToolCallback<?>, C extends BridgeToolChannel> {

    /**
     * Returns the set of tags associated with this tool. Tags are used for categorization
     * and discovery of tools.
     *
     * @return A list of string tags
     */
    Set<String> tags();

    /**
     * Creates a tool callback instance for this tool. The callback is the executable
     * representation of the tool that can be invoked by clients.
     *
     * @param channelHolder The channel holder providing communication capabilities
     * @return A new tool callback instance
     */
    TC createToolCallback(BridgeToolChannelHolder<C> channelHolder);

    default TC createToolCallback() {
        return createToolCallback(toolGroup().toolGroupSet().registration().getChannelHolder());
    }

    BridgeToolGroup<? extends BridgeTool<TC, C>, C> toolGroup();
}
