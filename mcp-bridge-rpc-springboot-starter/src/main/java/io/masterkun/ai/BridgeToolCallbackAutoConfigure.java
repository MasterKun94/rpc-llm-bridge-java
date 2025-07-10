package io.masterkun.ai;

import io.masterkun.ai.tool.BridgeToolCallback;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class BridgeToolCallbackAutoConfigure implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(BridgeToolCallbackAutoConfigure.class);

    private final ApplicationContext context;

    @Autowired
    public BridgeToolCallbackAutoConfigure(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Bean
    @ConditionalOnProperty(name = "mcp-bridge.auto-detect.enabled", havingValue = "true")
    public ToolCallbackProvider bridgeToolCallbackProvider() {
        List<ToolCallback> callbacks = new ArrayList<>();
        for (BridgeToolCallback<?> value : context.getBeansOfType(BridgeToolCallback.class).values()) {
            LOG.info("Registering bridge tool callback: {}", value.getToolDefinition().name());
            callbacks.add(new ProxyToolCallback<>(value));
        }
        return ToolCallbackProvider.from(callbacks);
    }
}
