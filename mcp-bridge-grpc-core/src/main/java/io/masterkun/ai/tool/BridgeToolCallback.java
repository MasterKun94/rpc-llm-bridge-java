package io.masterkun.ai.tool;

/**
 * Interface for tool callbacks in the MCP bridge framework.
 * This interface defines the contract for components that can execute tool operations
 * and return results of a specific type.
 *
 * @param <T> The type of result returned by the tool
 */
public interface BridgeToolCallback<T> {

    /**
     * Returns the definition of this tool.
     * The definition contains metadata about the tool such as name, description, and input schema.
     *
     * @return The tool definition
     */
    BridgeToolDefinition<T> getToolDefinition();

    /**
     * Returns the metadata associated with this tool.
     * The metadata provides additional information about the tool's capabilities and requirements.
     *
     * @return The tool metadata
     */
    default BridgeToolMetadata getToolMetadata() {
        return new BridgeToolMetadata() {
        };
    }

    /**
     * Executes the tool operation with the provided input.
     * This method processes the input string according to the tool's functionality
     * and returns a result of type T.
     *
     * @param toolInput The input string for the tool operation
     * @return The result of the tool operation
     */
    T call(String toolInput);
}
