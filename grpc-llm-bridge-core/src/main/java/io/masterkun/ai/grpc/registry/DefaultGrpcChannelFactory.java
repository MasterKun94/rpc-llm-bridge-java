package io.masterkun.ai.grpc.registry;

import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class DefaultGrpcChannelFactory implements GrpcChannelFactory {

    @Override
    public ManagedChannel create(String target, GrpcChannelOptions options) {
        NettyChannelBuilder builder = NettyChannelBuilder.forTarget(target);
        return update(builder, options).build();
    }

    protected NettyChannelBuilder update(NettyChannelBuilder builder, GrpcChannelOptions options) {
        if (options == null) {
            return builder;
        }

        // Configure usePlaintext
        if (options.getNegotiationType() != null) {
            builder.negotiationType(options.getNegotiationType());
        }

        // Configure maxInboundMessageSize
        if (options.getMaxInboundMessageSize() != null) {
            builder.maxInboundMessageSize(options.getMaxInboundMessageSize());
        }

        // Configure maxInboundMetadataSize
        if (options.getMaxInboundMetadataSize() != null) {
            builder.maxInboundMetadataSize(options.getMaxInboundMetadataSize());
        }

        // Configure keepAliveTime
        if (options.getKeepAliveTime() != null) {
            builder.keepAliveTime(options.getKeepAliveTime().toMillis(), MILLISECONDS);
        }

        // Configure keepAliveTimeout
        if (options.getKeepAliveTimeout() != null) {
            builder.keepAliveTimeout(options.getKeepAliveTimeout().toMillis(), MILLISECONDS);
        }

        // Configure keepAliveWithoutCalls
        if (options.getKeepAliveWithoutCalls() != null) {
            builder.keepAliveWithoutCalls(options.getKeepAliveWithoutCalls());
        }

        // Configure idleTimeout
        if (options.getIdleTimeout() != null) {
            builder.idleTimeout(options.getIdleTimeout().toMillis(), MILLISECONDS);
        }

        // Configure retry
        if (options.getEnableRetry() != null) {
            if (options.getEnableRetry()) {
                builder.enableRetry();
            } else {
                builder.disableRetry();
            }
        }

        // Configure maxRetryAttempts
        if (options.getMaxRetryAttempts() != null) {
            builder.maxRetryAttempts(options.getMaxRetryAttempts());
        }

        // Configure flowControlWindow
        if (options.getFlowControlWindow() != null) {
            builder.flowControlWindow(options.getFlowControlWindow());
        }

        // Configure initialFlowControlWindow
        if (options.getInitialFlowControlWindow() != null) {
            builder.initialFlowControlWindow(options.getInitialFlowControlWindow());
        }

        // Configure maxHedgedAttempts
        if (options.getMaxHedgedAttempts() != null) {
            builder.maxHedgedAttempts(options.getMaxHedgedAttempts());
        }

        // Configure retryBufferSize
        if (options.getRetryBufferSize() != null) {
            builder.retryBufferSize(options.getRetryBufferSize());
        }

        // Configure perRpcBufferLimit
        if (options.getPerRpcBufferLimit() != null) {
            builder.perRpcBufferLimit(options.getPerRpcBufferLimit());
        }

        // Configure overrideAuthority
        if (options.getOverrideAuthority() != null) {
            builder.overrideAuthority(options.getOverrideAuthority());
        }

        return builder;
    }
}
