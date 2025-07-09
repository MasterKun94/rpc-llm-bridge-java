package io.masterkun.ai;

import com.google.protobuf.Message;
import io.grpc.ManagedChannel;
import io.grpc.MethodDescriptor;
import jakarta.annotation.Nonnull;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.DefaultToolDefinition;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.util.ParsingUtils;

import java.util.function.Function;

public class GrpcToolCallback implements ToolCallback {
    private final MethodDescriptor<?, ?> method;
    private final ManagedChannel channel;
    private final Function<Message, String> resultConverter;

    public GrpcToolCallback(MethodDescriptor<?, ?> method, ManagedChannel channel) {
        this.method = method;
        this.channel = channel;
        this.resultConverter = getResultConverter(method);
    }

    public Function<Message, String> getResultConverter(MethodDescriptor<?, ?> method) {
        return message -> {
            return "";
        };
    }

    @Nonnull
    @Override
    public ToolDefinition getToolDefinition() {
        DefaultToolDefinition.Builder builder = ToolDefinition.builder();
        String methodName = GrpcUtils.getMethodName(method);
        builder.name(methodName);
        String methodDesc = GrpcUtils.getMethodDesc(method);
        if (methodDesc == null) {
            builder.description(ParsingUtils.reConcatenateCamelCase(methodName, " "));
        } else {
            builder.description(methodDesc);
        }
        builder.inputSchema(GrpcUtils.getInputSchema(method));
        return builder.build();
    }

    @Nonnull
    @Override
    public String call(@Nonnull String toolInput) {
        return "";
    }
}
