package io.masterkun.ai.registry;

import io.masterkun.ai.tool.BridgeToolCallback;

import java.util.List;

public interface BridgeToolDiscovery {
    List<BridgeToolCallback<?>> findToolCallbacks(String groupName);

    List<BridgeToolCallback<?>> findToolCallbacks(List<String> tags);

    List<BridgeToolCallback<?>> findToolCallbacks(String groupName, List<String> tags);
}
