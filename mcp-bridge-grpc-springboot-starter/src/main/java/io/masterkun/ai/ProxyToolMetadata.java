package io.masterkun.ai;

import io.masterkun.ai.tool.BridgeToolMetadata;
import org.springframework.ai.tool.metadata.ToolMetadata;

/**
 * Adapter class that bridges between MCP BridgeToolMetadata and Spring AI ToolMetadata.
 * This class wraps a BridgeToolMetadata instance and implements the Spring AI ToolMetadata interface,
 * allowing MCP bridge tool metadata to be used within the Spring AI framework.
 */
public class ProxyToolMetadata implements ToolMetadata {
    private final BridgeToolMetadata delegate;

    /**
     * Constructs a ProxyToolMetadata that wraps the specified BridgeToolMetadata.
     *
     * @param delegate The BridgeToolMetadata to wrap
     */
    public ProxyToolMetadata(BridgeToolMetadata delegate) {
        this.delegate = delegate;
    }

    /**
     * Indicates whether the tool's result should be returned directly without further processing.
     * Delegates to the wrapped BridgeToolMetadata.
     *
     * @return true if the result should be returned directly, false otherwise
     */
    @Override
    public boolean returnDirect() {
        return delegate.returnDirect();
    }
}
