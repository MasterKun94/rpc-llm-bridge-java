package io.masterkun.ai.registry;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * In-memory implementation of BridgeToolRegistrationStore that stores registrations in a
 * ConcurrentHashMap.
 */
public class MemoryBridgeToolRegistrationStore implements BridgeToolRegistrationStore {
    /**
     * Map storing registrations by name
     */
    private final Map<String, BridgeToolRegistration<?, ?, ?>> groupSetMap =
            new ConcurrentHashMap<>();

    /**
     * Adds a registration to the store, throwing an exception if it already exists.
     */
    @Override
    public void add(BridgeToolRegistration<?, ?, ?> groupSet) {
        BridgeToolRegistration<?, ?, ?> prev = groupSetMap.putIfAbsent(groupSet.name(), groupSet);
        if (prev != null) {
            throw new IllegalArgumentException("Registration already exists: " + groupSet.name());
        }
    }

    /**
     * Updates a registration by replacing it with a new instance.
     */
    @Override
    public void update(BridgeToolRegistration<?, ?, ?> groupSet) {
        update(groupSet.name(), r -> groupSet);
    }

    /**
     * Updates a registration using a function that transforms the existing registration.
     */
    @Override
    public void update(String name, Function<BridgeToolRegistration<?, ?, ?>, BridgeToolRegistration<?, ?, ?>> updater) {
        groupSetMap.compute(name, (n, r) -> {
            if (r == null) {
                throw new IllegalArgumentException("Registration does not exist: " + name);
            }
            BridgeToolRegistration<?,?,?> newed = updater.apply(r);
            if (newed == null) {
                throw new IllegalArgumentException("Registration cannot be null: " + name);
            }
            if (newed != r) {
                r.close();
            }
            return newed;
        });
    }

    /**
     * Removes a registration from the store and closes it.
     */
    @Override
    public void remove(String name) {
        BridgeToolRegistration<?,?,?> remove = groupSetMap.remove(name);
        if (remove != null) {
            remove.close();
        }
    }

    /**
     * Retrieves a registration by name, throwing an exception if it doesn't exist.
     */
    @Override
    public BridgeToolRegistration<?,?,?> get(String name) {
        BridgeToolRegistration<?,?,?> get = groupSetMap.get(name);
        if (get == null) {
            throw new IllegalArgumentException("Registration does not exist: " + name);
        }
        return get;
    }

    /**
     * Checks if a registration with the given name exists in the store.
     */
    @Override
    public boolean contains(String name) {
        return groupSetMap.containsKey(name);
    }

    /**
     * Searches for tools matching the specified tags and group name.
     */
    @Override
    public List<BridgeTool<?,?>> search(List<String> tags, String groupName) {
        Stream<BridgeToolRegistration<?,?,?>> rStream;
        if (groupName != null && !groupName.isBlank()) {
            rStream = Optional.<BridgeToolRegistration<?,?,?>>ofNullable(groupSetMap.get(groupName)).stream();
        } else {
            rStream = groupSetMap.values().stream();
        }
        Stream<BridgeTool<?,?>> tStream = rStream
                .flatMap(r -> r.groupSet().getGroups().stream())
                .flatMap(g -> g.tools().stream());
        if (tags != null && !tags.isEmpty()) {
            tStream = tStream.filter(t -> t.tags().containsAll(tags));
        }
        return tStream.toList();
    }
}
