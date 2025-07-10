package io.masterkun;

import io.masterkun.ai.grpc.ProtoUtils;
import io.masterkun.mcp.proto.ForTestProto;
import io.masterkun.mcp.proto.ForTestServiceGrpc;

public class TestMain {
    public static void main(String[] args) {
        System.out.println(ProtoUtils.getJsonSchema(ForTestProto.TestReq.getDescriptor()));

        System.out.println(ForTestServiceGrpc.getTestGetMethod().getFullMethodName());
        System.out.println(ForTestServiceGrpc.getTestGetMethod().getBareMethodName());
    }
}
