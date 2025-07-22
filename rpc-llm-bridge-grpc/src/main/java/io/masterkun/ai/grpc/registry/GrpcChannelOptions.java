package io.masterkun.ai.grpc.registry;

import io.grpc.netty.NegotiationType;

import java.time.Duration;

/**
 * Configuration options for gRPC channels used in the bridge framework.
 */
public class GrpcChannelOptions {
    private NegotiationType negotiationType;
    private Integer maxInboundMessageSize;
    private Integer maxInboundMetadataSize;
    private Duration keepAliveTime;
    private Duration keepAliveTimeout;
    private Boolean keepAliveWithoutCalls;
    private Duration idleTimeout;
    private Boolean enableRetry;
    private Integer maxRetryAttempts;
    private Integer flowControlWindow;
    private Integer initialFlowControlWindow;
    private Integer maxHedgedAttempts;
    private Long retryBufferSize;
    private Long perRpcBufferLimit;
    private String overrideAuthority;

    public NegotiationType getNegotiationType() {
        return negotiationType;
    }

    public void setNegotiationType(NegotiationType negotiationType) {
        this.negotiationType = negotiationType;
    }

    public Integer getMaxInboundMessageSize() {
        return maxInboundMessageSize;
    }

    public void setMaxInboundMessageSize(Integer maxInboundMessageSize) {
        this.maxInboundMessageSize = maxInboundMessageSize;
    }

    public Duration getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(Duration keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public Duration getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    public void setKeepAliveTimeout(Duration keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    public Duration getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(Duration idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public Boolean getEnableRetry() {
        return enableRetry;
    }

    public void setEnableRetry(Boolean enableRetry) {
        this.enableRetry = enableRetry;
    }

    public Integer getMaxInboundMetadataSize() {
        return maxInboundMetadataSize;
    }

    public void setMaxInboundMetadataSize(Integer maxInboundMetadataSize) {
        this.maxInboundMetadataSize = maxInboundMetadataSize;
    }

    public Boolean getKeepAliveWithoutCalls() {
        return keepAliveWithoutCalls;
    }

    public void setKeepAliveWithoutCalls(Boolean keepAliveWithoutCalls) {
        this.keepAliveWithoutCalls = keepAliveWithoutCalls;
    }

    public Integer getMaxRetryAttempts() {
        return maxRetryAttempts;
    }

    public void setMaxRetryAttempts(Integer maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    public Integer getFlowControlWindow() {
        return flowControlWindow;
    }

    public void setFlowControlWindow(Integer flowControlWindow) {
        this.flowControlWindow = flowControlWindow;
    }

    public Integer getInitialFlowControlWindow() {
        return initialFlowControlWindow;
    }

    public void setInitialFlowControlWindow(Integer initialFlowControlWindow) {
        this.initialFlowControlWindow = initialFlowControlWindow;
    }

    public Integer getMaxHedgedAttempts() {
        return maxHedgedAttempts;
    }

    public void setMaxHedgedAttempts(Integer maxHedgedAttempts) {
        this.maxHedgedAttempts = maxHedgedAttempts;
    }

    public Long getRetryBufferSize() {
        return retryBufferSize;
    }

    public void setRetryBufferSize(Long retryBufferSize) {
        this.retryBufferSize = retryBufferSize;
    }

    public Long getPerRpcBufferLimit() {
        return perRpcBufferLimit;
    }

    public void setPerRpcBufferLimit(Long perRpcBufferLimit) {
        this.perRpcBufferLimit = perRpcBufferLimit;
    }

    public String getOverrideAuthority() {
        return overrideAuthority;
    }

    public void setOverrideAuthority(String overrideAuthority) {
        this.overrideAuthority = overrideAuthority;
    }
}
