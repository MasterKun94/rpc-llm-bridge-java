package io.masterkun.ai.registry;

import java.util.Map;

public interface BridgeToolRegistration<T extends BridgeToolGroupSet<?>> {
    String name();
    String targetAddress();
    Map<String, String> options();
    T groupSet();
}
