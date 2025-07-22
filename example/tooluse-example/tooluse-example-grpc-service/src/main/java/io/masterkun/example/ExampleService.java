package io.masterkun.example;

import io.grpc.stub.StreamObserver;
import io.masterkun.toolcall.proto.ExampleProto;
import io.masterkun.toolcall.proto.ExampleServiceGrpc;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Example gRPC service implementation that provides string manipulation and time-related
 * functionalities. This service demonstrates how to implement a simple gRPC service that can be
 * exposed through the MCP Gateway.
 */
public class ExampleService extends ExampleServiceGrpc.ExampleServiceImplBase {
    public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss" +
                                                                            ".SSS");

    /**
     * Converts the input message to uppercase.
     */
    @Override
    public void toUpperCase(ExampleProto.ToUpperCaseReq request,
                            StreamObserver<ExampleProto.ToUpperCaseRes> responseObserver) {
        responseObserver.onNext(ExampleProto.ToUpperCaseRes.newBuilder()
                .setMessage(request.getMessage().toUpperCase())
                .build());
        responseObserver.onCompleted();
    }

    /**
     * Retrieves the current time in the specified timezone.
     */
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
