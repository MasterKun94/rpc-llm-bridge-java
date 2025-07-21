package io.masterkun.ai.grpc.registry;

import io.masterkun.ai.registry.BridgeToolGroupSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class GrpcBridgeToolGroupSet implements BridgeToolGroupSet<GrpcBridgeToolGroup> {

    @Override
    public List<GrpcBridgeToolGroup> getGroups() {
        return List.of();
    }

    @Override
    public void addGroup(GrpcBridgeToolGroup group) {

    }

    @Override
    public void updateGroup(GrpcBridgeToolGroup group) {

    }

    @Override
    public void removeGroup(String name) {

    }

    @Override
    public GrpcBridgeToolGroup getGroup(String name) {
        return null;
    }

    @Override
    public boolean containsGroup(String name) {
        return false;
    }

    @Override
    public void reload(InputStream inputStream) throws IOException {

    }

    @Override
    public void save(OutputStream outputStream) throws IOException {

    }
}
