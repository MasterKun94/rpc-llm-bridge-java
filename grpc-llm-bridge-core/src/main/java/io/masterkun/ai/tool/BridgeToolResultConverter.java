package io.masterkun.ai.tool;

/**
 * Interface for converting tool results to string representations in the MCP bridge framework.
 * This interface defines a conversion mechanism that transforms the native result type
 * of a tool into a string format suitable for returning to callers.
 *
 * @param <T> The type of result to convert
 */
public interface BridgeToolResultConverter<T> {
    /**
     * Converts a tool result to its string representation.
     * Implementations should provide a meaningful and human-readable
     * conversion of the result object.
     *
     * @param result The tool result to convert
     * @return A string representation of the result
     */
    String convert(T result);
}
