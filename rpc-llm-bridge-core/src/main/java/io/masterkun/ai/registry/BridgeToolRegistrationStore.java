package io.masterkun.ai.registry;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * Interface for storing and managing tool registrations in the bridge framework.
 */
public interface BridgeToolRegistrationStore {
    /**
     * Adds a new registration to the store.
     */
    void add(BridgeToolRegistration<?,?,?> groupSet);

    /**
     * Updates an existing registration in the store.
     */
    void update(BridgeToolRegistration<?,?,?> groupSet);

    /**
     * Updates an existing registration using a function.
     */
    void update(String name, Function<BridgeToolRegistration<?,?,?>, BridgeToolRegistration<?,?,?>> updater);

    /**
     * Removes a registration from the store.
     */
    void remove(String name);

    /**
     * Retrieves a registration by name.
     */
    BridgeToolRegistration<?,?,?> get(String name);

    /**
     * Checks if a registration exists in the store.
     */
    boolean contains(String name);

    /**
     * Searches for tools matching the specified tags and group name.
     */
    List<BridgeTool<?, ?>> search(@Nullable List<String> tags, @Nullable String groupName);
}
