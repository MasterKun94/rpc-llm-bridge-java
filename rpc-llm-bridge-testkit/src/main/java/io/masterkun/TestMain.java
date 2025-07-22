package io.masterkun;

import io.masterkun.ai.grpc.ProtoUtils;
import io.masterkun.toolcall.proto.ForTestProto;
import io.masterkun.toolcall.proto.ForTestServiceGrpc;

public class TestMain {
    public static void main(String[] args) {
        System.out.println(ProtoUtils.getJsonSchema(ForTestProto.TestReq.getDescriptor()));

        System.out.println(ForTestServiceGrpc.getTestGetMethod().getFullMethodName());
        System.out.println(ForTestServiceGrpc.getTestGetMethod().getBareMethodName());
    }
}
