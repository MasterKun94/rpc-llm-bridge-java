package io.masterkun.ai.tool;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BridgeToolContext {
    public static BridgeToolContext EMPTY = new BridgeToolContext(Collections.emptyMap());
    public static final String TOOL_CALL_HISTORY = "TOOL_CALL_HISTORY";

    private final Map<String, Object> context;

    public BridgeToolContext(Map<String, Object> context) {
        this.context = Collections.unmodifiableMap(context);
    }

    public Map<String, Object> getContext() {
        return context;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getToolCallHistory() {
        return (List<Map<String, Object>>) context.get(TOOL_CALL_HISTORY);
    }

    @Override
    public String toString() {
        return context.toString();
    }
}
