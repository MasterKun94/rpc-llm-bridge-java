package io.masterkun.ai;

import io.grpc.stub.StreamObserver;
import io.masterkun.mcp.proto.ExampleProto;
import io.masterkun.mcp.proto.ExampleServiceGrpc;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ExampleService extends ExampleServiceGrpc.ExampleServiceImplBase {
    public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public void toUpperCase(ExampleProto.ToUpperCaseReq request,
                            StreamObserver<ExampleProto.ToUpperCaseRes> responseObserver) {
        responseObserver.onNext(ExampleProto.ToUpperCaseRes.newBuilder()
                .setMessage(request.getMessage().toUpperCase())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getTime(ExampleProto.ZonedTimeReq request,
                        StreamObserver<ExampleProto.ZonedTimeRes> responseObserver) {
        ZoneId zoneId = ZoneId.of(request.getTimezone());
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        responseObserver.onNext(ExampleProto.ZonedTimeRes.newBuilder()
                .setDatetime(now.format(FORMATTER))
                .build());
        responseObserver.onCompleted();
    }
}
