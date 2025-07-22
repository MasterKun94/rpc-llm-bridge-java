package io.masterkun.ai.registry;

import java.util.Map;

public interface BridgeToolChannelFactory<C extends BridgeToolChannel> {
    C create(String targetAddress, Map<String, String> channelOptions);
}
