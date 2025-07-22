package io.masterkun.ai.registry;

/**
 * Interface representing a registry for tool registrations in the rpc-llm bridge framework.
 * This registry serves as the central repository for all registered tool sets, providing
 * methods to register, unregister, and look up tool registrations.
 * <p>
 * The registry is a key component of the service registration and discovery mechanism,
 * acting as the main entry point for publishing and finding tools in the system.
 */
public interface BridgeToolRegistry<T extends BridgeToolRegistration<?>> {
    /**
     * Registers a tool registration with this registry, making its tools available
     * for discovery and use by clients.
     *
     * @param registration The tool registration to register
     */
    void register(T registration);

    /**
     * Unregisters a tool registration from this registry by name, removing its tools
     * from availability.
     *
     * @param name The name of the registration to unregister
     */
    void unregister(String name);

    /**
     * Checks if a registration with the specified name exists in this registry.
     *
     * @param name The name to check
     * @return true if a registration with the specified name exists, false otherwise
     */
    boolean containsRegistration(String name);

    /**
     * Retrieves a registration by name from this registry.
     *
     * @param name The name of the registration to retrieve
     * @return The registration with the specified name
     */
    T getRegistration(String name);
}
