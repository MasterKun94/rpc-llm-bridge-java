package io.masterkun.ai.tool;

public interface BridgeToolResultConverter<T> {
    String convert(T result);
}
