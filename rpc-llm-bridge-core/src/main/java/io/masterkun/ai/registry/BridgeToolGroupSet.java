package io.masterkun.ai.registry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Interface representing a collection of tool groups in the rpc-llm bridge framework.
 * This interface provides methods for managing groups of tools, including adding,
 * updating, removing, and retrieving groups. It also supports persistence operations
 * and automatic discovery of tools.
 * <p>
 * The tool group set is a central component in the tool registry system, serving as
 * a container for organizing related tools into logical groups.
 */
public interface BridgeToolGroupSet<G extends BridgeToolGroup<T, C>, C extends BridgeToolChannel, T extends BridgeTool<?, C>> {
    /**
     * Returns all tool groups in this set.
     *
     * @return A list of all tool groups
     */
    List<G> getGroups();

    /**
     * Adds a new tool group to this set.
     *
     * @param group The tool group to add
     */
    void addGroup(G group);

    /**
     * Updates an existing tool group in this set.
     *
     * @param group The tool group with updated information
     */
    void updateGroup(G group);

    /**
     * Removes a tool group from this set by name.
     *
     * @param name The name of the tool group to remove
     */
    void removeGroup(String name);

    /**
     * Retrieves a tool group by name.
     *
     * @param name The name of the tool group to retrieve
     * @return The tool group with the specified name
     */
    G getGroup(String name);

    /**
     * Checks if a tool group with the specified name exists in this set.
     *
     * @param name The name to check
     * @return true if a group with the specified name exists, false otherwise
     */
    boolean containsGroup(String name);

    /**
     * Reloads this tool group set from the specified input stream.
     * This method typically deserializes the tool group set from a persistent format.
     *
     * @param inputStream The input stream to read from
     * @throws IOException If an I/O error occurs during reading
     */
    void reload(InputStream inputStream) throws IOException;

    /**
     * Saves this tool group set to the specified output stream.
     * This method typically serializes the tool group set to a persistent format.
     *
     * @param outputStream The output stream to write to
     * @throws IOException If an I/O error occurs during writing
     */
    void save(OutputStream outputStream) throws IOException;

    /**
     * Reloads this tool group set by automatically discovering available tools
     * using the provided channel holder.
     *
     * @param channelHolder The channel holder to use for communication during discovery
     * @throws IOException If an I/O error occurs during discovery
     */
    void reloadByAutoDiscovery(BridgeToolChannelHolder<C> channelHolder) throws IOException;

    BridgeToolRegistration<? extends BridgeToolGroupSet<G, C, T>, C, T> registration();
}
