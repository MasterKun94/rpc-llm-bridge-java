package io.masterkun.ai;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;

public class GrpcToolCallback implements ToolCallback {
    @Override
    public ToolDefinition getToolDefinition() {
        return ToolDefinition.builder().build()
                ;
    }

    @Override
    public ToolMetadata getToolMetadata() {
        return ToolCallback.super.getToolMetadata();
    }

    @Override
    public String call(String toolInput) {
        return "";
    }

    @Override
    public String call(String toolInput, ToolContext tooContext) {
        return ToolCallback.super.call(toolInput, tooContext);
    }
}
