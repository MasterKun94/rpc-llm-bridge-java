package io.masterkun.ai.registry;

/**
 * Interface representing a communication channel in the rpc-llm bridge framework. This interface
 * extends AutoCloseable to ensure proper resource management for channels that may hold
 * network connections or other resources that need to be released when no longer in use.
 * <p>
 * Implementations of this interface provide the underlying communication mechanism for
 * tool invocations across the bridge.
 */
public interface BridgeToolChannel extends AutoCloseable {
}
