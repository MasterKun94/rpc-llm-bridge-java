package io.masterkun.ai.registry;

import io.masterkun.ai.tool.BridgeToolCallback;

import java.util.List;

public interface BridgeTool<T extends BridgeToolCallback<?>> {

    List<String> tags();

    T createToolCallback(Object serviceChannel);
}
