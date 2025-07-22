package io.masterkun.ai.registry;

import io.masterkun.ai.tool.BridgeToolCallback;

import java.util.List;

public interface BridgeTool<T extends BridgeToolCallback<?>, C extends BridgeToolChannel> {

    List<String> tags();

    T createToolCallback(BridgeToolChannelHolder<C> channelHolder);
}
