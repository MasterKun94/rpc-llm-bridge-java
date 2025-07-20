package io.masterkun.ai.tool;

/**
 * Interface defining the metadata and capabilities of a tool in the MCP bridge framework.
 * This interface provides essential information about a tool, including its name,
 * description, input requirements, and result conversion capabilities.
 *
 * @param <T> The type of result produced by the tool
 */
public interface BridgeToolDefinition<T> {

    /**
     * Returns the name of the tool.
     * The name should be unique and descriptive of the tool's functionality.
     *
     * @return The tool name
     */
    String name();

    /**
     * Returns the description of the tool.
     * The description should provide a clear explanation of what the tool does
     * and how it should be used.
     *
     * @return The tool description
     */
    String description();

    /**
     * Returns the schema for the tool's input.
     * The schema defines the structure and constraints of valid inputs for the tool.
     *
     * @return The input schema as a string (typically in JSON Schema format)
     */
    String inputSchema();

    /**
     * Returns a provider for creating result converters for this tool.
     * The result converter transforms the tool's native result type to the desired output format.
     *
     * @return A provider for creating result converters
     */
    BridgeToolResultConverterProvider<T> resultConverter();
}
