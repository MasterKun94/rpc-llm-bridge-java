package io.masterkun.ai.registry;

import java.util.Map;

/**
 * Interface representing a registration of a tool group set in the rpc-llm bridge framework.
 * A registration associates a tool group set with a name, target address, and configuration
 * options, making it available for discovery and use by clients.
 * <p>
 * This interface is a key part of the service registration mechanism, allowing tools to be
 * published and made available through the bridge.
 */
public interface BridgeToolRegistration<T extends BridgeToolGroupSet<?, ?>> {
    /**
     * Returns the name of this registration. The name serves as a unique identifier
     * for the registration within a registry.
     *
     * @return The name of the registration
     */
    String name();

    /**
     * Returns the target address associated with this registration. The target address
     * specifies the location or endpoint where the registered tools can be accessed.
     *
     * @return The target address
     */
    String targetAddress();

    /**
     * Returns the configuration options for this registration. These options provide
     * additional parameters for establishing connections and invoking the registered tools.
     *
     * @return A map of configuration options
     */
    Map<String, String> options();

    /**
     * Returns the tool group set associated with this registration. The group set
     * contains the actual tools that are being registered.
     *
     * @return The tool group set
     */
    T groupSet();
}
