package io.masterkun.ai.registry;

import java.util.List;

/**
 * Interface representing a group of related tools in the rpc-llm bridge framework.
 * Tool groups provide a way to organize and categorize tools, making them easier
 * to discover and manage. Each group has a name and contains a collection of tools.
 */
public interface BridgeToolGroup<T extends BridgeTool<?, C>, C extends BridgeToolChannel> {

    /**
     * Returns the name of this tool group. The name serves as a unique identifier
     * for the group within a tool group set.
     *
     * @return The name of the tool group
     */
    String name();

    /**
     * Returns the list of tools contained in this group. These tools share a common
     * categorization or purpose as defined by the group.
     *
     * @return A list of tools in this group
     */
    List<T> tools();

    BridgeToolGroupSet<? extends BridgeToolGroup<T, C>, C, T> toolGroupSet();
}
