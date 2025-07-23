# gRPC Auto Tool Discovery Example

This directory contains a complete example demonstrating how to use the auto tool discovery feature to integrate with gRPC services, enabling automatic discovery and invocation of large model tools.

## Component Overview

The example consists of two main components:

1. **auto-discovery-example-grpc-service**: A simple gRPC service that provides two methods:
   - `toUpperCase`: Converts a string to uppercase
   - `getTime`: Gets the current time for a specified timezone

2. **auto-discovery-example-caller-service**: A Spring Boot application that integrates Spring AI and auto tool discovery functionality, allowing large models to invoke automatically discovered gRPC services through natural language.

## Startup Sequence

Please start the components in the following order:

### 1. Start the gRPC Service

```bash
cd auto-discovery-example-grpc-service
mvn spring-boot:run
```

After successful startup, the console will display: `Example GRPC server started`

This service will start a gRPC server on local port 8080 and enable gRPC reflection service, allowing clients to automatically discover service definitions.

### 2. Start the Auto Discovery Client

Before starting the client, make sure to set the DashScope API key environment variable:

```bash
export DASHSCOPE_API_KEY=your_api_key_here
```

Then start the client service:

```bash
cd auto-discovery-example-caller-service
mvn spring-boot:run
```

This service will start a Spring Boot application on local port 8888, providing a REST API to handle natural language requests.

## Testing the Example

After starting all services, you can test the example in the following ways:

### Testing the toUpperCase Functionality

Send an HTTP request to the client's `/client/stream/chat` endpoint, asking to convert text to uppercase:

```bash
curl "http://localhost:8888/client/stream/chat?query=convert 'test' to uppercase"
```

### Testing the getTime Functionality

Send an HTTP request to the client's `/client/stream/chat` endpoint, asking for the current time in a specific region:

```bash
curl "http://localhost:8888/client/stream/chat?query=what is the current time in Washington, USA"
```

## Expected Results

### toUpperCase Functionality

When you request to convert text to uppercase, the large model will call the toUpperCase tool and return a response similar to the following:

```
I have converted the text 'test' to uppercase: TEST
```

### getTime Functionality

When you request the current time for a specific region, the large model will call the getTime tool and return a response similar to the following:

```
The current time in Washington, USA is: 2023-06-01 12:34:56.789
```

Note: The actual returned time will be the current time at the moment of the request.

## How It Works

1. The client automatically discovers tools provided by the gRPC service at startup
2. The client receives natural language requests from users
3. Spring AI processes the request and decides to call the appropriate tool
4. Through the Tool Callback mechanism, Spring AI calls the automatically discovered tool
5. The tool calls the gRPC service via a gRPC client
6. The gRPC service processes the request and returns results
7. Results are returned to Spring AI through Tool Callback
8. Spring AI integrates the results into a natural language response and returns it to the user

## Differences from Other Examples

Unlike the tooluse-example, this example uses the auto tool discovery feature, eliminating the need to manually define and register tools. The system automatically discovers tools provided by the gRPC service at startup and registers them in the tool registry.

Unlike the mcp-example, this example directly uses Spring AI's Tool Callback mechanism without the need for an intermediate MCP Bridge service, and it can automatically discover tools without manual configuration.
