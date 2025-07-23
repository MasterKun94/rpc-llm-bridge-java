package io.masterkun.example;

import io.masterkun.ai.ProxyToolDiscovery;
import io.masterkun.ai.grpc.registry.GrpcBridgeToolRegistration;
import io.masterkun.ai.registry.BridgeToolRegistry;
import io.masterkun.ai.registry.DefaultBridgeToolRegistry;
import io.masterkun.ai.registry.MemoryBridgeToolRegistrationStore;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类，设置工具注册表和自动发现功能。
 */
@Configuration
public class ExampleToolConfig {

    /**
     * 创建工具注册表Bean，用于存储工具注册信息。
     */
    @Bean
    public BridgeToolRegistry bridgeToolRegistry() {
        return new DefaultBridgeToolRegistry(new MemoryBridgeToolRegistrationStore());
    }

    /**
     * 创建工具发现服务Bean，用于发现已注册的工具。
     */
    @Bean
    public ProxyToolDiscovery bridgeToolDiscovery(BridgeToolRegistry registry) {
        return new ProxyToolDiscovery(registry);
    }

    /**
     * 创建注册表初始化器Bean，用于初始化工具注册表。
     */
    @Bean
    public RegistryInitializer registryInitializer(BridgeToolRegistry registry) {
        return new RegistryInitializer(registry);
    }

    /**
     * 注册表初始化器内部类，实现自动工具发现和注册。
     */
    public static class RegistryInitializer implements InitializingBean {
        private final BridgeToolRegistry registry;

        /**
         * 构造函数，初始化注册表引用。
         */
        public RegistryInitializer(BridgeToolRegistry registry) {
            this.registry = registry;
        }

        /**
         * 在Bean属性设置完成后自动执行，实现工具的自动发现和注册。
         */
        @Override
        public void afterPropertiesSet() throws Exception {
            GrpcBridgeToolRegistration registration = new GrpcBridgeToolRegistration("localhost:8080");
            registration.reloadByAutoDiscovery();
            registry.register(registration);
        }
    }
}
