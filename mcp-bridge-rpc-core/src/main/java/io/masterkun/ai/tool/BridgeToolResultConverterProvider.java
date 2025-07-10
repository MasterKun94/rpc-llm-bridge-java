package io.masterkun.ai.tool;

public interface BridgeToolResultConverterProvider<T> {
    BridgeToolResultConverter<T> get();
}
