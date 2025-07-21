package io.masterkun.ai.grpc.registry;

import com.google.protobuf.Descriptors;
import io.masterkun.ai.grpc.ProtoParser;
import io.masterkun.ai.proto.ToolProto;
import io.masterkun.ai.registry.BridgeToolGroupSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GrpcBridgeToolGroupSet implements BridgeToolGroupSet<GrpcBridgeToolGroup> {
    private final Map<String, GrpcBridgeToolGroup> groups = new LinkedHashMap<>();

    @Override
    public List<GrpcBridgeToolGroup> getGroups() {
        return new ArrayList<>(groups.values());
    }

    @Override
    public void addGroup(GrpcBridgeToolGroup group) {
        if (groups.containsKey(group.name())) {
            throw new IllegalArgumentException("Group already exists: " + group.name());
        }
        groups.put(group.name(), group);
    }

    @Override
    public void updateGroup(GrpcBridgeToolGroup group) {
        if (!groups.containsKey(group.name())) {
            throw new IllegalArgumentException("Group does not exist: " + group.name());
        }
        groups.put(group.name(), group);
    }

    @Override
    public void removeGroup(String name) {
        if (!groups.containsKey(name)) {
            throw new IllegalArgumentException("Group does not exist: " + name);
        }
        groups.remove(name);
    }

    @Override
    public GrpcBridgeToolGroup getGroup(String name) {
        if (!groups.containsKey(name)) {
            throw new IllegalArgumentException("Group does not exist: " + name);
        }
        return groups.get(name);
    }

    @Override
    public boolean containsGroup(String name) {
        return groups.containsKey(name);
    }

    @Override
    public void reload(InputStream inputStream) throws IOException {
        ToolProto.BridgeToolGroupSet proto = ToolProto.BridgeToolGroupSet.parseFrom(inputStream);
        reload(proto);
    }

    public void reload(ToolProto.BridgeToolGroupSet proto) throws IOException {
        Map<String, Descriptors.FileDescriptor> allDependencies =
                ProtoParser.load(proto.getAllDependencies());
        groups.clear();
        for (ToolProto.BridgeToolGroup group : proto.getGroupsList()) {
            String name = group.getName();
            List<GrpcBridgeTool> tools = new ArrayList<>();
            for (ToolProto.BridgeTool tool : group.getToolsList()) {
                Descriptors.FileDescriptor fileDescriptor = allDependencies.get(tool.getFilename());
                if (fileDescriptor == null) {
                    throw new IllegalArgumentException("File descriptor not found: " + tool.getFilename());
                }
                Descriptors.ServiceDescriptor service =
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

    @Override
    public void save(OutputStream outputStream) throws IOException {
        ToolProto.BridgeToolGroupSet proto = save();
        proto.writeTo(outputStream);
    }

    public ToolProto.BridgeToolGroupSet save() throws IOException {
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
        return builder.setAllDependencies(ProtoParser.save(reduce.values())).build();
    }
}
