package io.masterkun.ai.tool;

/**
 * Interface for providing result converters in the MCP bridge framework.
 * This interface acts as a factory for creating result converter instances,
 * allowing the framework to obtain new converters when needed.
 *
 * @param <T> The type of result that the provided converters can handle
 */
public interface BridgeToolResultConverterProvider<T> {
    /**
     * Creates and returns a new result converter instance.
     * This method should return a fresh converter that can transform
     * results of type T to string representations.
     *
     * @return A new result converter instance
     */
    BridgeToolResultConverter<T> get();
}
