package io.masterkun;

import io.masterkun.ai.ProtoUtils;

public class TestMain {
    public static void main(String[] args) {
        System.out.println(ProtoUtils.getJsonSchema(io.masterkun.mcp.proto.ForTestProto.TestReq.getDescriptor()));
    }
}
