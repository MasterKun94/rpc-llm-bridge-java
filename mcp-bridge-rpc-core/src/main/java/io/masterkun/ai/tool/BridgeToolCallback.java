package io.masterkun.ai.tool;

public interface BridgeToolCallback<T> {

    BridgeToolDefinition<T> getToolDefinition();

    default BridgeToolMetadata getToolMetadata() {
        return new BridgeToolMetadata() {
        };
    }

    T call(String toolInput);
}
