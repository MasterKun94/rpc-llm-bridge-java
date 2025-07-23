# gRPC Tool Callback Example

This directory contains a complete example demonstrating how to integrate Spring AI's Tool Callback functionality with gRPC services to implement large model tool invocation.

## Component Overview

The example consists of two main components:

1. **tooluse-example-grpc-service**: A simple gRPC service that provides two methods:
   - `toUpperCase`: Converts a string to uppercase
   - `getTime`: Gets the current time for a specified timezone

2. **tooluse-example-caller-service**: A Spring Boot application that integrates Spring AI and Tool Callback functionality, allowing large models to invoke gRPC services through natural language.

## Startup Sequence

Please start the components in the following order:

### 1. Start the gRPC Service

```bash
cd tooluse-example-grpc-service
mvn spring-boot:run
```

After successful startup, the console will display: `Example GRPC server started`

This service will start a gRPC server on local port 8080.

### 2. Start the Tool Callback Client

Before starting the client, make sure to set the DashScope API key environment variable:

```bash
export DASHSCOPE_API_KEY=your_api_key_here
```

Then start the client service:

```bash
cd tooluse-example-caller-service
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

1. The client receives natural language requests from users
2. Spring AI processes the request and decides to call the appropriate tool
3. Through the Tool Callback mechanism, Spring AI calls the registered tool
4. The tool calls the gRPC service via a gRPC client
5. The gRPC service processes the request and returns results
6. Results are returned to Spring AI through Tool Callback
7. Spring AI integrates the results into a natural language response and returns it to the user

## Differences from the MCP Example

Unlike the MCP example, this example directly uses Spring AI's Tool Callback mechanism without the need for an intermediate MCP Bridge service. This approach is more direct and suitable for integrating tool invocation functionality in applications that already use Spring AI.
