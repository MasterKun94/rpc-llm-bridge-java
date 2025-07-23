package io.masterkun.ai.registry;

import io.masterkun.ai.tool.BridgeToolCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Default implementation of BridgeToolRegistry that uses a BridgeToolRegistrationStore for storage.
 */
public class DefaultBridgeToolRegistry implements BridgeToolRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultBridgeToolRegistry.class);

    /** The store used to manage registrations */
    private final BridgeToolRegistrationStore store;

    /**
     * Creates a new registry with the specified registration store.
     */
    public DefaultBridgeToolRegistry(BridgeToolRegistrationStore store) {
        this.store = store;
    }

    /**
     * Registers a tool registration by adding it to the store.
     */
    @Override
    public void register(BridgeToolRegistration<?,?,?> registration) {
        store.add(registration);
        LOG.info("Register tools: {}", registration);
    }

    @Override
    public void registerByAutoDiscovery(BridgeToolRegistration<?, ?, ?> registration) {

    }

    /**
     * Updates an existing registration in the store.
     */
    @Override
    public void updateRegistration(BridgeToolRegistration<?,?,?> registration) {
        store.update(registration);
    }

    /**
     * Unregisters a registration by removing it from the store.
     */
    @Override
    public void unregister(String name) {
        store.remove(name);
    }

    /**
     * Checks if a registration exists by delegating to the store.
     */
    @Override
    public boolean containsRegistration(String name) {
        return store.contains(name);
    }

    /**
     * Retrieves a registration by delegating to the store.
     */
    @Override
    public BridgeToolRegistration<?,?,?> getRegistration(String name) {
        return store.get(name);
    }

    /**
     * Creates a new discovery client for this registry.
     */
    @Override
    public BridgeToolDiscovery getDiscovery() {
        return new DefaultBridgeToolDiscovery();
    }

    /**
     * Default implementation of BridgeToolDiscovery that uses the registry's store for searching.
     */
    public class DefaultBridgeToolDiscovery implements BridgeToolDiscovery {

        /**
         * Finds all tool callbacks by delegating to the overloaded method with null parameters.
         */
        @Override
        public List<BridgeToolCallback<?>> findToolCallbacks() {
            return findToolCallbacks(null, null);
        }

        /**
         * Finds tool callbacks by group name, delegating to the overloaded method with null tags.
         */
        @Override
        public List<BridgeToolCallback<?>> findToolCallbacks(String groupName) {
            return findToolCallbacks(groupName, null);
        }

        /**
         * Finds tool callbacks by tags, delegating to the overloaded method with null group name.
         */
        @Override
        public List<BridgeToolCallback<?>> findToolCallbacks(List<String> tags) {
            return findToolCallbacks(null, tags);
        }

        /**
         * Finds tool callbacks by searching the store and creating callbacks from the results.
         */
        @Override
        public List<BridgeToolCallback<?>> findToolCallbacks(String groupName, List<String> tags) {
            return store.search(tags, groupName).stream()
                    .<BridgeToolCallback<?>>map(BridgeTool::createToolCallback)
                    .toList();
        }
    }
}
