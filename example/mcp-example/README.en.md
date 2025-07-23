# gRPC MCP Bridge Example

This directory contains a complete example demonstrating how to convert gRPC services to MCP services through MCP Bridge, and how to invoke these services using a client.

## Component Overview

The example consists of three main components:

1. **mcp-example-grpc-service**: A simple gRPC service that provides two methods:
   - `toUpperCase`: Converts a string to uppercase
   - `getTime`: Gets the current time for a specified timezone

2. **mcp-server-grpc-bridge**: A Spring Boot application that acts as an MCP server, converting gRPC services to MCP services.

3. **mcp-client**: A Spring Boot application that acts as an MCP client, invoking MCP services through a large model.

## Startup Sequence

Please start the components in the following order:

### 1. Start the gRPC Service

```bash
cd example-grpc-service
mvn spring-boot:run
```

After successful startup, the console will display: `Example GRPC server started`

This service will start a gRPC server on local port 8080.

### 2. Start the MCP Bridge

```bash
cd mcp-server-grpc-bridge
mvn spring-boot:run
```

This service will convert gRPC services to MCP services and start a Spring Boot application on local port 8081.

### 3. Start the MCP Client

```bash
cd mcp-client
mvn spring-boot:run
```

This service will start a Spring Boot application on local port 8888, providing a REST API to invoke MCP services.

## Testing the Example

After starting all services, you can test the example in the following ways:

### Testing the toUpperCase Functionality

Send an HTTP request to the client's `/client/stream/chat` endpoint, asking to convert text to uppercase:

```bash
curl "http://localhost:8888/client/stream/chat?query=please convert the text 'hello world' to uppercase"
```

### Testing the getTime Functionality

Send an HTTP request to the client's `/client/stream/chat` endpoint, asking for the current time in a specific timezone:

```bash
curl "http://localhost:8888/client/stream/chat?query=please tell me the current time in UTC+8 timezone"
```

## Expected Results

### toUpperCase Functionality

When you request to convert text to uppercase, the large model will call the toUpperCase tool and return a response similar to the following:

```
I have converted the text 'hello world' to uppercase: HELLO WORLD
```

### getTime Functionality

When you request the current time for a specific timezone, the large model will call the getTime tool and return a response similar to the following:

```
The current time in UTC+8 timezone is: 2023-06-01 12:34:56.789
```

Note: The actual returned time will be the current time at the moment of the request.

## How It Works

1. The client receives natural language requests from users
2. The large model understands the request and decides to call the appropriate tool
3. The large model calls the tools provided by the Bridge through the MCP protocol
4. The Bridge converts MCP requests to gRPC requests and calls the gRPC service
5. The gRPC service processes the request and returns results
6. The Bridge converts gRPC responses to MCP responses and returns them to the large model
7. The large model integrates the results into a natural language response and returns it to the user
