package io.masterkun.ai.registry;

public interface BridgeToolRegistry<T extends BridgeToolRegistration<?>> {
    void register(T registration);
    void unregister(String name);
    boolean containsRegistration(String name);
    T getRegistration(String name);
}
