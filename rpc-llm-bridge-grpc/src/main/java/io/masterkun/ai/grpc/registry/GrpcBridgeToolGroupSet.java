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

public class GrpcBridgeToolGroupSet implements BridgeToolGroupSet<GrpcBridgeToolGroup,
        GrpcBridgeToolChannel> {
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

    @Override
    public void save(OutputStream outputStream) throws IOException {
        ToolProto.BridgeToolGroupSet proto = save();
        proto.writeTo(outputStream);
    }

    @Override
    public void reloadByAutoDiscovery(BridgeToolChannelHolder<GrpcBridgeToolChannel> channelHolder) throws IOException {
        ServerReflectionGrpc.ServerReflectionBlockingV2Stub stub =
                ServerReflectionGrpc.newBlockingV2Stub(channelHolder.get().channel());
        try {

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
            reloadByAutoDiscovery(allServices, allFileDescriptors);
        } catch (StatusException e) {
            throw new IOException(e);
        } catch (InterruptedException e) {
            throw new InterruptedIOException();
        }
    }

    public void reloadByAutoDiscovery(Set<String> services,
                                      Map<String, Descriptors.FileDescriptor> fileDescriptors) {
        for (Descriptors.FileDescriptor file : fileDescriptors.values()) {
            DescriptorProtos.FileOptions fileOpt = file.getOptions();
            boolean fileEnabled = fileOpt.hasExtension(ToolProto.fileAutoDiscovery) &&
                                  fileOpt.getExtension(ToolProto.fileAutoDiscovery);
            for (ServiceDescriptor service : file.getServices()) {
                if (!services.contains(service.getFullName())) {
                    continue;
                }
                DescriptorProtos.ServiceOptions serviceOpt = service.getOptions();
                boolean serviceEnabled = serviceOpt.hasExtension(ToolProto.serviceAutoDiscovery) &&
                                         serviceOpt.getExtension(ToolProto.serviceAutoDiscovery);
                String groupName = serviceOpt.hasExtension(ToolProto.groupName) ?
                        serviceOpt.getExtension(ToolProto.groupName) :
                        service.getFullName();
                List<String> serviceTags = serviceOpt.hasExtension(ToolProto.serviceTags) ?
                        serviceOpt.getExtension(ToolProto.serviceTags) :
                        List.of();
                List<GrpcBridgeTool> tools = new ArrayList<>();
                for (Descriptors.MethodDescriptor method : service.getMethods()) {
                    DescriptorProtos.MethodOptions methodOpt = method.getOptions();
                    boolean methodEnabled = methodOpt.hasExtension(ToolProto.methodAutoDiscovery) &&
                                            methodOpt.getExtension(ToolProto.methodAutoDiscovery);
                    if (fileEnabled || serviceEnabled || methodEnabled) {
                        Set<String> methodTags = methodOpt.hasExtension(ToolProto.methodTags) ?
                                new LinkedHashSet<>(methodOpt.getExtension(ToolProto.methodTags)) :
                                new LinkedHashSet<>();
                        methodTags.addAll(serviceTags);
                        tools.add(new GrpcBridgeTool(List.copyOf(methodTags), method));
                    }
                }
                if (!tools.isEmpty()) {
                    addGroup(new GrpcBridgeToolGroup(groupName, tools));
                }
            }
        }
    }

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
