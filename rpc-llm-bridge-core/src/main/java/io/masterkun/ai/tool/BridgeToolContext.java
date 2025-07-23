package io.masterkun.ai.tool;

import java.util.Collections;
import java.util.Map;

public class BridgeToolContext {
    public static BridgeToolContext EMPTY = new BridgeToolContext(Collections.emptyMap());

    private final Map<String, Object> context;

    public BridgeToolContext(Map<String, Object> context) {
        this.context = Collections.unmodifiableMap(context);
    }

    public Map<String, Object> getContext() {
        return context;
    }
}
