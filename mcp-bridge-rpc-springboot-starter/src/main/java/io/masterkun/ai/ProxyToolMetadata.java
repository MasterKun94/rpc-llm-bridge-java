package io.masterkun.ai;

import io.masterkun.ai.tool.BridgeToolMetadata;
import org.springframework.ai.tool.metadata.ToolMetadata;

public class ProxyToolMetadata implements ToolMetadata {
    private final BridgeToolMetadata delegate;

    public ProxyToolMetadata(BridgeToolMetadata delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean returnDirect() {
        return delegate.returnDirect();
    }
}
