package io.masterkun.ai.tool;

/**
 * Interface for providing additional metadata about tools in the rpc-llm bridge framework. This
 * interface allows tools to specify behavioral characteristics and processing options that affect
 * how the tool's results are handled by the framework.
 */
public interface BridgeToolMetadata {

    /**
     * Indicates whether the tool's result should be returned directly without further processing.
     * When true, the framework will return the tool's result as-is to the caller. When false
     * (default), the framework may apply additional processing or transformations to the result
     * before returning it.
     *
     * @return true if the result should be returned directly, false otherwise
     */
    default boolean returnDirect() {
        return false;
    }
}
