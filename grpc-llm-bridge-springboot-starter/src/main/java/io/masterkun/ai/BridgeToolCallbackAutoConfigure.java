package io.masterkun.ai;

import io.masterkun.ai.tool.BridgeToolCallback;
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

/**
 * Spring Boot auto-configuration class for MCP bridge tools. This class automatically detects and
 * registers BridgeToolCallback instances as Spring AI ToolCallback objects, enabling seamless
 * integration between the MCP bridge framework and Spring AI.
 */
@Configuration(proxyBeanMethods = false)
public class BridgeToolCallbackAutoConfigure implements InitializingBean {
    private static final Logger LOG =
            LoggerFactory.getLogger(BridgeToolCallbackAutoConfigure.class);

    private final ApplicationContext context;

    /**
     * Constructs a BridgeToolCallbackAutoConfigure with the specified application context.
     *
     * @param context The Spring application context
     */
    @Autowired
    public BridgeToolCallbackAutoConfigure(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void afterPropertiesSet() {

    }

    /**
     * Creates a ToolCallbackProvider that contains all BridgeToolCallback instances found in the
     * application context. This bean is only created when the 'mcp-bridge.auto-detect.enabled'
     * property is set to 'true'.
     *
     * @return A ToolCallbackProvider containing proxied BridgeToolCallback instances
     */
    @Bean
    @ConditionalOnProperty(name = "mcp-bridge.auto-detect.enabled", havingValue = "true")
    public ToolCallbackProvider bridgeToolCallbackProvider() {
        List<ToolCallback> callbacks = new ArrayList<>();
        for (BridgeToolCallback<?> value :
                context.getBeansOfType(BridgeToolCallback.class).values()) {
            LOG.info("Registering bridge tool callback: {}", value.getToolDefinition().name());
            callbacks.add(new ProxyToolCallback<>(value));
        }
        return ToolCallbackProvider.from(callbacks);
    }
}
