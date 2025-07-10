package io.masterkun.ai;

import io.masterkun.ai.tool.BridgeToolCallback;
import io.masterkun.ai.tool.BridgeToolResultConverter;
import jakarta.annotation.Nonnull;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;

public class ProxyToolCallback<T> implements ToolCallback {
    private final BridgeToolCallback<T> delegate;
    private final BridgeToolResultConverter<T> converter;

    public ProxyToolCallback(BridgeToolCallback<T> delegate) {
        this.delegate = delegate;
        this.converter = delegate.getToolDefinition().resultConverter().get();
    }

    @Nonnull
    @Override
    public ToolDefinition getToolDefinition() {
        return new ProxyToolDefinition(delegate.getToolDefinition());
    }

    @Nonnull
    @Override
    public ToolMetadata getToolMetadata() {
        return new ProxyToolMetadata(delegate.getToolMetadata());
    }

    @Nonnull
    @Override
    public String call(@Nonnull String toolInput) {
        return converter.convert(delegate.call(toolInput));
    }
}
