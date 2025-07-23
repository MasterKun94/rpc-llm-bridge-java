package io.masterkun.ai.registry;

import java.io.IOException;
import java.util.Map;

/**
 * Interface representing a registration of a tool group set in the rpc-llm bridge framework. A
 * registration associates a tool group set with a name, target address, and configuration options,
 * making it available for discovery and use by clients.
 * <p>
 * This interface is a key part of the service registration mechanism, allowing tools to be
 * published and made available through the bridge.
 */
public interface BridgeToolRegistration<S extends BridgeToolGroupSet<?, C, T>,
        C extends BridgeToolChannel, T extends BridgeTool<?, C>> extends AutoCloseable {
    /**
     * Returns the name of this registration. The name serves as a unique identifier for the
     * registration within a registry.
     *
     * @return The name of the registration
     */
    String name();

    /**
     * Returns the target address associated with this registration. The target address specifies
     * the location or endpoint where the registered tools can be accessed.
     *
     * @return The target address
     */
    String targetAddress();

    /**
     * Returns the configuration options for this registration. These options provide additional
     * parameters for establishing connections and invoking the registered tools.
     *
     * @return A map of configuration options
     */
    Map<String, String> options();

    /**
     * Returns the tool group set associated with this registration. The group set contains the
     * actual tools that are being registered.
     *
     * @return The tool group set
     */
    S groupSet();

    /**
     * Returns the channel holder associated with this registration.
     */
    BridgeToolChannelHolder<C> getChannelHolder();

    /**
     * Reloads the associated tool group set by performing automatic discovery of available tools.
     * This method utilizes the channel holder associated with the current registration to facilitate
     * communication during the discovery process.
     */
    default void reloadByAutoDiscovery() throws IOException {
        groupSet().reloadByAutoDiscovery(getChannelHolder());
    }

    default void close() {
        getChannelHolder().close();
    }
}
