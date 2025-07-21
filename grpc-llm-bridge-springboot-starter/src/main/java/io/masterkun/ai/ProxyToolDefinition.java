package io.masterkun.ai;

import io.masterkun.ai.tool.BridgeToolDefinition;
import jakarta.annotation.Nonnull;
import org.springframework.ai.tool.definition.ToolDefinition;

/**
 * Adapter class that bridges between MCP BridgeToolDefinition and Spring AI ToolDefinition. This
 * class wraps a BridgeToolDefinition instance and implements the Spring AI ToolDefinition
 * interface, allowing MCP bridge tool definitions to be used within the Spring AI framework.
 */
public class ProxyToolDefinition implements ToolDefinition {
    private final BridgeToolDefinition<?> delegate;

    /**
     * Constructs a ProxyToolDefinition that wraps the specified BridgeToolDefinition.
     *
     * @param delegate The BridgeToolDefinition to wrap
     */
    public ProxyToolDefinition(BridgeToolDefinition<?> delegate) {
        this.delegate = delegate;
    }

    /**
     * Returns the name of the tool. Delegates to the wrapped BridgeToolDefinition.
     *
     * @return The tool name
     */
    @Nonnull
    @Override
    public String name() {
        return delegate.name();
    }

    /**
     * Returns the description of the tool. Delegates to the wrapped BridgeToolDefinition.
     *
     * @return The tool description
     */
    @Nonnull
    @Override
    public String description() {
        return delegate.description();
    }

    /**
     * Returns the schema for the tool's input. Delegates to the wrapped BridgeToolDefinition.
     *
     * @return The input schema as a string
     */
    @Nonnull
    @Override
    public String inputSchema() {
        return delegate.inputSchema();
    }
}
