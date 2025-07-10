# gRPC MCP Bridge Example

This directory contains a complete example demonstrating how to convert a gRPC service to an MCP service through the MCP Bridge, and how to call the service using a client.

## Component Overview

The example consists of three main components:

1. **example-grpc-service**: A simple gRPC service that provides two methods:
   - `toUpperCase`: Converts a string to uppercase
   - `getTime`: Gets the current time in a specified timezone

2. **example-grpc-bridge-springboot**: A Spring Boot application that serves as an MCP Bridge, converting the gRPC service to an MCP service.

3. **example-client-springboot**: A Spring Boot application that serves as an MCP client, calling the MCP service through a large language model.

## Startup Sequence

Please start the components in the following order:

### 1. Start the gRPC Service

```bash
cd example-grpc-service
mvn spring-boot:run
```

After successful startup, the console will display: `Example GRPC server started`

This service will start a gRPC server on port 8080 on your local machine.

### 2. Start the MCP Bridge

```bash
cd example-grpc-bridge-springboot
mvn spring-boot:run
```

This service will convert the gRPC service to an MCP service and start a Spring Boot application on port 8081 on your local machine.

### 3. Start the MCP Client

```bash
cd example-client-springboot
mvn spring-boot:run
```

This service will start a Spring Boot application on port 8888 on your local machine, providing a REST API to call the MCP service.

## Testing the Example

After starting all services, you can test the example in the following ways:

### Testing the toUpperCase Functionality

Send an HTTP request to the client's `/client/stream/chat` endpoint, asking to convert text to uppercase:

```bash
curl "http://localhost:8888/client/stream/chat?query=Please convert the text 'hello world' to uppercase"
```

### Testing the getTime Functionality

Send an HTTP request to the client's `/client/stream/chat` endpoint, asking for the current time in a specific timezone:

```bash
curl "http://localhost:8888/client/stream/chat?query=Please tell me the current time in UTC+8 timezone"
```

## Expected Results

### toUpperCase Functionality

When you request to convert text to uppercase, the large language model will call the toUpperCase tool and return a response similar to the following:

```
I have converted the text 'hello world' to uppercase: HELLO WORLD
```

### getTime Functionality

When you request the current time in a specific timezone, the large language model will call the getTime tool and return a response similar to the following:

```
The current time in UTC+8 timezone is: 2023-06-01 12:34:56.789
```

Note: The actual returned time will be the current time at the moment of the request.

## How It Works

1. The client receives a natural language request from the user
2. The large language model understands the request and decides to call the appropriate tool
3. The large language model calls the tool provided by the Bridge through the MCP protocol
4. The Bridge converts the MCP request to a gRPC request and calls the gRPC service
5. The gRPC service processes the request and returns the result
6. The Bridge converts the gRPC response to an MCP response and returns it to the large language model
7. The large language model integrates the result into a natural language response and returns it to the user
