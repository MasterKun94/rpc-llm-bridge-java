package io.masterkun.example;

import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.Descriptors;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.reflection.v1.ListServiceResponse;
import io.grpc.reflection.v1.ServerReflectionGrpc;
import io.grpc.reflection.v1.ServerReflectionRequest;
import io.grpc.reflection.v1.ServiceResponse;
import io.masterkun.ai.grpc.ProtoUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReflectionClient {
    public static void main(String[] args) throws Exception {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();
        var stub = ServerReflectionGrpc.newBlockingV2Stub(channel);
        var call = stub.serverReflectionInfo();
        call.write(ServerReflectionRequest.newBuilder()
                .setListServices("")
                .build());
        ListServiceResponse listServicesResponse = call.read().getListServicesResponse();
        System.out.println(listServicesResponse);
        List<String> services = listServicesResponse.getServiceList().stream()
                .map(ServiceResponse::getName)
                .collect(Collectors.toList());

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
                FileDescriptorProto proto = FileDescriptorProto.parseFrom(bytes);
                builder.addFile(proto);
            }
            Map<String, Descriptors.FileDescriptor> load = ProtoUtils.load(builder.build());
            allFileDescriptors.putAll(load);
            for (Descriptors.FileDescriptor value : load.values()) {
                for (Descriptors.ServiceDescriptor valueService : value.getServices()) {
                    services.remove(valueService.getFullName());
                }
            }
        }
        for (Descriptors.FileDescriptor value : allFileDescriptors.values()) {
            System.out.println(value.getName());
        }
    }
}
