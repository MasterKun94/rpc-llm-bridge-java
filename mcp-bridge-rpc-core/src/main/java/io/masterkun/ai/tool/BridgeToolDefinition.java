
package io.masterkun.ai.tool;

public interface BridgeToolDefinition<T> {

	String name();

	String description();

	String inputSchema();

	BridgeToolResultConverterProvider<T> resultConverter();
}
