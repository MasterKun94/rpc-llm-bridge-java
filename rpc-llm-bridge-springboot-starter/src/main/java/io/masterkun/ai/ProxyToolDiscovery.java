package io.masterkun.ai;

import io.masterkun.ai.registry.BridgeToolDiscovery;
import io.masterkun.ai.registry.BridgeToolRegistry;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;

public class ProxyToolDiscovery {
    private final BridgeToolDiscovery delegate;

    public ProxyToolDiscovery(BridgeToolDiscovery delegate) {
        this.delegate = delegate;
    }

    public ProxyToolDiscovery(BridgeToolRegistry registry) {
        this(registry.getDiscovery());
    }

    public List<ToolCallback> findToolCallbacks() {
        return findToolCallbacks(null, null);
    }

    public List<ToolCallback> findToolCallbacks(String groupName) {
        return findToolCallbacks(groupName, null);
    }

    public List<ToolCallback> findToolCallbacks(List<String> tags) {
        return findToolCallbacks(null, tags);
    }

    public List<ToolCallback> findToolCallbacks(String groupName, List<String> tags) {
        return delegate.findToolCallbacks(groupName, tags).stream()
                .<ToolCallback>map(ProxyToolCallback::new)
                .toList();
    }
}
