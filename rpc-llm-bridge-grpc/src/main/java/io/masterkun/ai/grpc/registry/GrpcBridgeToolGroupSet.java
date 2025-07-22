package io.masterkun.ai.grpc.registry;

import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.ServiceDescriptor;
import io.grpc.StatusException;
import io.grpc.reflection.v1.ServerReflectionGrpc;
import io.grpc.reflection.v1.ServerReflectionRequest;
import io.grpc.reflection.v1.ServiceResponse;
import io.masterkun.ai.grpc.ProtoUtils;
import io.masterkun.ai.proto.ToolProto;
import io.masterkun.ai.registry.BridgeToolChannelHolder;
import io.masterkun.ai.registry.BridgeToolGroupSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A gRPC implementation of the BridgeToolGroupSet interface that manages collections of gRPC tool
 * groups and provides functionality for discovery, loading, and saving of tool definitions.
 */
public class GrpcBridgeToolGroupSet implements BridgeToolGroupSet<GrpcBridgeToolGroup,
        GrpcBridgeToolChannel> {
    private final Map<String, GrpcBridgeToolGroup> groups = new LinkedHashMap<>();

    /**
     * Returns a list of all tool groups in this set.
     *
     * @return A list containing all tool groups
     */
    @Override
    public List<GrpcBridgeToolGroup> getGroups() {
        return new ArrayList<>(groups.values());
    }

    /**
     * Adds a new tool group to this set.
     *
     * @param group The tool group to add
     * @throws IllegalArgumentException if a group with the same name already exists
     */
    @Override
    public void addGroup(GrpcBridgeToolGroup group) {
        if (groups.containsKey(group.name())) {
            throw new IllegalArgumentException("Group already exists: " + group.name());
        }
        groups.put(group.name(), group);
    }

    /**
     * Updates an existing tool group in this set.
     *
     * @param group The tool group with updated content
     * @throws IllegalArgumentException if the group does not exist
     */
    @Override
    public void updateGroup(GrpcBridgeToolGroup group) {
        if (!groups.containsKey(group.name())) {
            throw new IllegalArgumentException("Group does not exist: " + group.name());
        }
        groups.put(group.name(), group);
    }

    /**
     * Removes a tool group from this set by name.
     *
     * @param name The name of the group to remove
     * @throws IllegalArgumentException if the group does not exist
     */
    @Override
    public void removeGroup(String name) {
        if (!groups.containsKey(name)) {
            throw new IllegalArgumentException("Group does not exist: " + name);
        }
        groups.remove(name);
    }

    /**
     * Retrieves a tool group by name.
     *
     * @param name The name of the group to retrieve
     * @return The tool group with the specified name
     * @throws IllegalArgumentException if the group does not exist
     */
    @Override
    public GrpcBridgeToolGroup getGroup(String name) {
        if (!groups.containsKey(name)) {
            throw new IllegalArgumentException("Group does not exist: " + name);
        }
        return groups.get(name);
    }

    /**
     * Checks if a tool group with the specified name exists in this set.
     *
     * @param name The name of the group to check
     * @return true if the group exists, false otherwise
     */
    @Override
    public boolean containsGroup(String name) {
        return groups.containsKey(name);
    }

    /**
     * Reloads tool groups from a serialized protobuf input stream.
     *
     * @param inputStream The input stream containing serialized tool group data
     * @throws IOException If an I/O error occurs during reading or parsing
     */
    @Override
    public void reload(InputStream inputStream) throws IOException {
        ToolProto.BridgeToolGroupSet proto = ToolProto.BridgeToolGroupSet.parseFrom(inputStream);
        reload(proto);
    }

    /**
     * Reloads tool groups from a protobuf BridgeToolGroupSet object. This method clears all
     * existing groups and loads new ones from the provided proto object.
     *
     * @param proto The protobuf object containing tool group definitions
     * @throws IOException              If an error occurs during loading file descriptors
     * @throws IllegalArgumentException If referenced file descriptors, services, or methods are not
     *                                  found
     */
    public void reload(ToolProto.BridgeToolGroupSet proto) throws IOException {
        groups.clear();
        Map<String, Descriptors.FileDescriptor> allDependencies =
                ProtoUtils.load(proto.getAllDependencies());
        for (ToolProto.BridgeToolGroup group : proto.getGroupsList()) {
            String name = group.getName();
            List<GrpcBridgeTool> tools = new ArrayList<>();
            for (ToolProto.BridgeTool tool : group.getToolsList()) {
                Descriptors.FileDescriptor fileDescriptor = allDependencies.get(tool.getFilename());
                if (fileDescriptor == null) {
                    throw new IllegalArgumentException("File descriptor not found: " + tool.getFilename());
                }
                ServiceDescriptor service =
                        fileDescriptor.findServiceByName(tool.getServiceName());
                if (service == null) {
                    throw new IllegalArgumentException("Service not found: " + tool.getServiceName());
                }
                Descriptors.MethodDescriptor method =
                        service.findMethodByName(tool.getMethodName());
                if (method == null) {
                    throw new IllegalArgumentException("Method not found: " + tool.getMethodName());
                }
                List<String> tags = List.copyOf(tool.getTagsList());
                tools.add(new GrpcBridgeTool(tags, method));
            }
            addGroup(new GrpcBridgeToolGroup(name, tools));
        }
    }

    /**
     * Saves the current tool groups to an output stream in protobuf format.
     *
     * @param outputStream The output stream to write the serialized data to
     * @throws IOException If an I/O error occurs during writing
     */
    @Override
    public void save(OutputStream outputStream) throws IOException {
        ToolProto.BridgeToolGroupSet proto = save();
        proto.writeTo(outputStream);
    }

    /**
     * Reloads tool groups by auto-discovering services using gRPC reflection. This method uses the
     * server reflection API to discover available services and their methods, then loads them as
     * tool groups based on their configuration.
     *
     * @param channelHolder The holder providing the gRPC channel for communication with the server
     * @throws IOException If an I/O error occurs during communication or if reflection fails
     */
    @Override
    public void reloadByAutoDiscovery(BridgeToolChannelHolder<GrpcBridgeToolChannel> channelHolder) throws IOException {
        // Create a blocking stub for server reflection
        ServerReflectionGrpc.ServerReflectionBlockingV2Stub stub =
                ServerReflectionGrpc.newBlockingV2Stub(channelHolder.get().channel());
        try {

            // Request and collect a list of all services from the server
            var call = stub.serverReflectionInfo();
            ServerReflectionRequest req = ServerReflectionRequest.newBuilder()
                    .setListServices("")
                    .build();
            if (!call.write(req)) {
                throw new IOException("Failed to write request");
            }
            List<String> services = call.read().getListServicesResponse()
                    .getServiceList()
                    .stream()
                    .map(ServiceResponse::getName)
                    .collect(Collectors.toList());
            Set<String> allServices = new HashSet<>(services);

            // Fetch file descriptors for each service
            Map<String, Descriptors.FileDescriptor> allFileDescriptors = new HashMap<>();
            while (!services.isEmpty()) {
                String service = services.remove(0);
                call.write(ServerReflectionRequest.newBuilder()
                        .setFileContainingSymbol(service)
                        .build());
                DescriptorProtos.FileDescriptorSet.Builder builder =
                        DescriptorProtos.FileDescriptorSet.newBuilder();
                for (ByteString bytes :
                        call.read().getFileDescriptorResponse().getFileDescriptorProtoList()) {
                    DescriptorProtos.FileDescriptorProto proto =
                            DescriptorProtos.FileDescriptorProto.parseFrom(bytes);
                    builder.addFile(proto);
                }
                Map<String, Descriptors.FileDescriptor> load = ProtoUtils.load(builder.build());
                allFileDescriptors.putAll(load);
                for (Descriptors.FileDescriptor value : load.values()) {
                    for (ServiceDescriptor valueService : value.getServices()) {
                        services.remove(valueService.getFullName());
                    }
                }
            }
            // Process collected services and file descriptors
            reloadByAutoDiscovery(allServices, allFileDescriptors);
        } catch (StatusException e) {
            throw new IOException(e);
        } catch (InterruptedException e) {
            throw new InterruptedIOException();
        }
    }

    /**
     * Reloads tool groups by analyzing the provided services and file descriptors. This method
     * examines the proto options on files, services, and methods to determine which ones should be
     * included as tools, and how they should be grouped.
     *
     * @param services        The set of service full names to consider for inclusion
     * @param fileDescriptors The map of file descriptors containing the services and methods
     */
    public void reloadByAutoDiscovery(Set<String> services,
                                      Map<String, Descriptors.FileDescriptor> fileDescriptors) {
        // Iterate through all file descriptors to check for auto-discovery settings
        for (Descriptors.FileDescriptor file : fileDescriptors.values()) {
            DescriptorProtos.FileOptions fileOpt = file.getOptions();
            Boolean fileEnabled = fileOpt.hasExtension(ToolProto.fileAutoDiscovery) ?
                    null : fileOpt.getExtension(ToolProto.fileAutoDiscovery);

            // Process each service in the file
            for (ServiceDescriptor service : file.getServices()) {
                if (!services.contains(service.getFullName())) {
                    continue;
                }

                // Extract service options, group name and tags
                DescriptorProtos.ServiceOptions serviceOpt = service.getOptions();
                Boolean serviceEnabled = serviceOpt.hasExtension(ToolProto.serviceAutoDiscovery) ?
                        null : serviceOpt.getExtension(ToolProto.serviceAutoDiscovery);
                String groupName = serviceOpt.hasExtension(ToolProto.groupName) ?
                        serviceOpt.getExtension(ToolProto.groupName) :
                        service.getFullName();
                List<String> serviceTags = serviceOpt.hasExtension(ToolProto.serviceTags) ?
                        serviceOpt.getExtension(ToolProto.serviceTags) :
                        List.of();

                List<GrpcBridgeTool> tools = new ArrayList<>();

                // Examine each method to determine if it should be included as a tool
                for (Descriptors.MethodDescriptor method : service.getMethods()) {
                    DescriptorProtos.MethodOptions methodOpt = method.getOptions();
                    Boolean methodEnabled = methodOpt.hasExtension(ToolProto.methodAutoDiscovery) ?
                            null : methodOpt.getExtension(ToolProto.methodAutoDiscovery);
                    /*
                     Determine if this method should be enabled as a tool based on a hierarchy of
                      settings:
                     1. Method-level setting takes the highest precedence (if specified)
                     2. Service-level setting is used if method-level is not specified
                     3. File-level setting is used only if both method and service levels are not
                      specified
                     Note: At the file level, the setting must be explicitly true to enable the tool
                    */
                    boolean toolEnabled = methodEnabled == null ?
                            serviceEnabled == null ?
                                    fileEnabled != null && fileEnabled :
                                    serviceEnabled :
                            methodEnabled;
                    if (toolEnabled) {
                        Set<String> methodTags = methodOpt.hasExtension(ToolProto.methodTags) ?
                                new LinkedHashSet<>(methodOpt.getExtension(ToolProto.methodTags)) :
                                new LinkedHashSet<>();
                        methodTags.addAll(serviceTags);
                        tools.add(new GrpcBridgeTool(List.copyOf(methodTags), method));
                    }
                }

                // Create a new tool group if tools were found
                if (!tools.isEmpty()) {
                    addGroup(new GrpcBridgeToolGroup(groupName, tools));
                }
            }
        }
    }

    /**
     * Creates a protobuf representation of the current tool groups. This method converts all tool
     * groups, tools, and their associated file descriptors into a serializable protobuf object.
     *
     * @return A protobuf BridgeToolGroupSet containing all tool group definitions
     */
    public ToolProto.BridgeToolGroupSet save() {
        ToolProto.BridgeToolGroupSet.Builder builder = ToolProto.BridgeToolGroupSet.newBuilder();
        for (GrpcBridgeToolGroup group : groups.values()) {
            ToolProto.BridgeToolGroup.Builder groupBuilder = ToolProto.BridgeToolGroup.newBuilder()
                    .setName(group.name());
            for (GrpcBridgeTool tool : group.tools()) {
                groupBuilder.addTools(ToolProto.BridgeTool.newBuilder()
                        .setFilename(tool.methodDescriptor().getFile().getName())
                        .setServiceName(tool.methodDescriptor().getService().getName())
                        .setMethodName(tool.methodDescriptor().getName())
                        .addAllTags(tool.tags())
                );
            }
            builder.addGroups(groupBuilder);
        }
        HashMap<String, Descriptors.FileDescriptor> reduce = groups.values().stream()
                .flatMap(g -> g.tools().stream())
                .map(tool -> tool.methodDescriptor().getFile())
                .reduce(new HashMap<>(), (map, file) -> {
                    map.put(file.getName(), file);
                    return map;
                }, (m1, m2) -> {
                    m1.putAll(m2);
                    return m1;
                });
        return builder.setAllDependencies(ProtoUtils.save(reduce.values())).build();
    }
}
