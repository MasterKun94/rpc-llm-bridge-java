package io.masterkun;

import io.grpc.MethodDescriptor;
import io.masterkun.ai.GrpcUtils;
import io.masterkun.mcp.proto.ForTestProto;
import io.masterkun.mcp.proto.ForTestServiceGrpc;

public class Test {
    public static void main(String[] args) {
        MethodDescriptor<ForTestProto.TestReq, ForTestProto.TestRes> method = ForTestServiceGrpc.getTestGetMethod();
        System.out.println(GrpcUtils.getMethodName(method));
        System.out.println(GrpcUtils.getMethodDesc(method));
        System.out.println("------");
        System.out.println(GrpcUtils.getInputSchema(method));
        System.out.println("------");
        System.out.println(GrpcUtils.getOutputSchema(method));
    }
}
