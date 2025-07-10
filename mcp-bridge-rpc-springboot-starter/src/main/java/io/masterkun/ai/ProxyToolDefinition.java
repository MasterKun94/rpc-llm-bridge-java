package io.masterkun.ai;

import io.masterkun.ai.tool.BridgeToolDefinition;
import jakarta.annotation.Nonnull;
import org.springframework.ai.tool.definition.ToolDefinition;

public class ProxyToolDefinition implements ToolDefinition {
    private final BridgeToolDefinition<?> delegate;

    public ProxyToolDefinition(BridgeToolDefinition<?> delegate) {
        this.delegate = delegate;
    }

    @Nonnull
    @Override
    public String name() {
        return delegate.name();
    }

    @Nonnull
    @Override
    public String description() {
        return delegate.description();
    }

    @Nonnull
    @Override
    public String inputSchema() {
        return delegate.inputSchema();
    }
}
