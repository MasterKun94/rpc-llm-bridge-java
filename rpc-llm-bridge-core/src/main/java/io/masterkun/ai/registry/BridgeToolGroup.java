package io.masterkun.ai.registry;

import java.util.List;

public interface BridgeToolGroup<T extends BridgeTool<?>> {

    String name();

    List<T> tools();
}
