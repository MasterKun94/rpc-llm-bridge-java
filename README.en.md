# rpc-llm-bridge-java

rpc-llm-bridge-java is a framework for large language model (LLM) tool calling that converts RPC services into tools for LLMs. With this framework, developers can easily integrate their RPC services into LLM tool calling frameworks, enabling LLMs to call various services.

[中文文档](README.md)

## Project Overview

rpc-llm-bridge-java provides a complete solution for converting RPC services into tools that can be called by LLMs. The framework has the following features:

- **Spring Boot Auto-Configuration Support**: Easy integration with Spring Boot projects
- **Automatic Tool Registration and Discovery**: Automatically discover and register RPC services as tools
- **Customizable Tool Call Parameters and Return Value Formats**: Flexible format definitions through proto extensions
- **Multiple Integration Methods**: Support for direct tool calling, MCP protocol integration, and more

## Main Modules

- **rpc-llm-bridge-core**: Core interface module that defines the basic interfaces for tool calling, registration, and discovery.
- **rpc-llm-bridge-grpc**: gRPC-based implementation module that inherits the interfaces defined in rpc-llm-bridge-core and provides a gRPC-based framework implementation.
- **rpc-llm-bridge-springboot-starter**: Spring Boot auto-configuration module that provides Spring Boot auto-configuration and integration, making it easy to use in Spring Boot projects. Main features:
  - Automatic configuration of tool callback mechanism
  - Automatic registration and discovery of tools
  - Seamless integration with Spring AI

## Example Projects

### auto-discovery-example

An example of automatic tool discovery and calling. This example demonstrates how to use the automatic tool discovery feature to integrate with gRPC services, enabling automatic discovery and calling of LLM tools.

For detailed documentation, see [auto-discovery-example README](example/auto-discovery-example/README.md).

Main components:
- gRPC service: Provides toUpperCase and getTime methods
- Auto-discovery client: Integrates Spring AI and automatic tool discovery functionality

Workflow:
1. The client automatically discovers tools provided by the gRPC service at startup
2. The client receives natural language requests from users
3. Spring AI processes the request and decides to call the appropriate tool
4. Through the Tool Callback mechanism, Spring AI calls the automatically discovered tool
5. The tool calls the gRPC service via the gRPC client
6. Results are returned to Spring AI via Tool Callback and ultimately to the user

### tooluse-example

Tool calling example. This example demonstrates how to use Spring AI's Tool Callback functionality to integrate with gRPC services, enabling LLM tool calling.

For detailed documentation, see [tooluse-example README](example/tooluse-example/README.md).

Main components:
- gRPC service: Provides toUpperCase and getTime methods
- Tool Callback client: Integrates Spring AI and Tool Callback functionality

Difference from the auto-discovery example: This example directly uses Spring AI's Tool Callback mechanism and requires manual definition and registration of tools.

### mcp-example

MCP calling example. This example demonstrates how to convert a gRPC service into an MCP service through an MCP Bridge, and how to use a client to call the service.

For detailed documentation, see [mcp-example README](example/mcp-example/README.md).

Main components:
- gRPC service: Provides toUpperCase and getTime methods
- MCP Bridge: Converts the gRPC service into an MCP service
- MCP client: Calls the MCP service through an LLM

Workflow:
1. The client receives natural language requests from users
2. The LLM understands the request and decides to call the appropriate tool
3. The LLM calls the tool provided by the Bridge via the MCP protocol
4. The Bridge converts the MCP request into a gRPC request and calls the gRPC service
5. Results are returned to the LLM via the Bridge and ultimately to the user

## Future Plans

The rpc-llm-bridge-java project plans to implement the following features in the future:

1. **Support for More RPC Frameworks**: In addition to gRPC, plans to add support for other RPC frameworks such as Dubbo and Thrift
2. **Enhanced Tool Discovery Mechanism**: Provide more flexible tool discovery and filtering mechanisms
3. **Richer Tool Metadata**: Support for more detailed tool descriptions and usage instructions
4. **Enhanced Security**: Provide more comprehensive security mechanisms, including authentication, authorization, and access control
5. **Monitoring and Statistics**: Add monitoring and statistics functionality for tool calls
6. **Support for More LLM Platforms**: Add support for more LLM platforms

## Contribution Guidelines

We welcome community contributions. If you are interested in participating in project development, please follow these steps:

1. Fork the project repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Create a Pull Request

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details.
