package io.masterkun.ai;

import io.masterkun.ai.tool.BridgeToolCallback;
import io.masterkun.ai.tool.BridgeToolContext;
import io.masterkun.ai.tool.BridgeToolResultConverter;
import jakarta.annotation.Nonnull;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;

/**
 * Adapter class that bridges between MCP BridgeToolCallback and Spring AI ToolCallback. This class
 * wraps a BridgeToolCallback instance and implements the Spring AI ToolCallback interface, allowing
 * MCP bridge tools to be used within the Spring AI framework.
 *
 * @param <T> The type of result returned by the wrapped BridgeToolCallback
 */
public class ProxyToolCallback<T> implements ToolCallback {
    private final BridgeToolCallback<T> delegate;
    private final BridgeToolResultConverter<T> converter;

    /**
     * Constructs a ProxyToolCallback that wraps the specified BridgeToolCallback. Initializes the
     * result converter from the wrapped callback's tool definition.
     *
     * @param delegate The BridgeToolCallback to wrap
     */
    public ProxyToolCallback(BridgeToolCallback<T> delegate) {
        this.delegate = delegate;
        this.converter = delegate.getToolDefinition().resultConverter().get();
    }

    /**
     * Returns the tool definition for this callback. Creates a ProxyToolDefinition that wraps the
     * delegate's tool definition.
     *
     * @return The tool definition
     */
    @Nonnull
    @Override
    public ToolDefinition getToolDefinition() {
        return new ProxyToolDefinition(delegate.getToolDefinition());
    }

    /**
     * Returns the tool metadata for this callback. Creates a ProxyToolMetadata that wraps the
     * delegate's tool metadata.
     *
     * @return The tool metadata
     */
    @Nonnull
    @Override
    public ToolMetadata getToolMetadata() {
        return new ProxyToolMetadata(delegate.getToolMetadata());
    }

    /**
     * Executes the tool operation with the provided input. Delegates the call to the wrapped
     * BridgeToolCallback and converts the result.
     *
     * @param toolInput The input string for the tool operation
     * @return The converted result as a string
     */
    @Nonnull
    @Override
    public String call(@Nonnull String toolInput) {
        return call(toolInput, null);
    }

    /**
     * Executes the tool operation with the provided input and tool context. This implementation
     * ignores the tool context and delegates to the simpler call method.
     *
     * @param toolInput  The input string for the tool operation
     * @param toolContext The tool context
     * @return The converted result as a string
     */
    @Nonnull
    @Override
    public String call(@Nonnull String toolInput, ToolContext toolContext) {
        BridgeToolContext bridgeToolContext = toolContext == null ?
                BridgeToolContext.EMPTY :
                new BridgeToolContext(toolContext.getContext());
        return converter.convert(delegate.call(toolInput, bridgeToolContext));
    }
}
