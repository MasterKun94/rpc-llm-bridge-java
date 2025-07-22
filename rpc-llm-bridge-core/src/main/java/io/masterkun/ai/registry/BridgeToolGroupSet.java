package io.masterkun.ai.registry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface BridgeToolGroupSet<T extends BridgeToolGroup<?>, C extends BridgeToolChannel> {
    List<T> getGroups();

    void addGroup(T group);

    void updateGroup(T group);

    void removeGroup(String name);

    T getGroup(String name);

    boolean containsGroup(String name);

    void reload(InputStream inputStream) throws IOException;

    void save(OutputStream outputStream) throws IOException;

    void reloadByAutoDiscovery(BridgeToolChannelHolder<C> channelHolder) throws IOException;
}
