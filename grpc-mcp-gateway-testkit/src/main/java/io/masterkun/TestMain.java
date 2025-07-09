package io.masterkun;

import io.masterkun.ai.ProtoUtils;
import io.masterkun.mcp.proto.ForTestProto;

public class TestMain {
    public static void main(String[] args) {
        System.out.println(ProtoUtils.getJsonSchema(ForTestProto.TestReq.getDescriptor()));
    }
}
