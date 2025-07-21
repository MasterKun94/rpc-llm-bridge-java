# gRPC MCP Bridge 示例

本目录包含了一个完整的示例，演示如何将 gRPC 服务通过 MCP Bridge 转换为 MCP 服务，并使用客户端调用该服务。

## 组件概述

示例由三个主要组件组成：

1. **example-grpc-service**：一个简单的 gRPC 服务，提供两个方法：
   - `toUpperCase`：将字符串转换为大写
   - `getTime`：获取指定时区的当前时间

2. **mcp-server-grpc-bridge**：一个 Spring Boot 应用，作为 MCP 服务端，将 gRPC 服务转换为 MCP 服务。

3. **mcp-client**：一个 Spring Boot 应用，作为 MCP 客户端，通过大模型调用 MCP 服务。

## 启动顺序

请按照以下顺序启动各个组件：

### 1. 启动 gRPC 服务

```bash
cd example-grpc-service
mvn spring-boot:run
```

启动成功后，控制台会显示：`Example GRPC server started`

这个服务会在本地的 8080 端口启动一个 gRPC 服务器。

### 2. 启动 MCP Bridge

```bash
cd mcp-server-grpc-bridge
mvn spring-boot:run
```

这个服务会将 gRPC 服务转换为 MCP 服务，并在本地的 8081 端口启动一个 Spring Boot 应用。

### 3. 启动 MCP 客户端

```bash
cd mcp-client
mvn spring-boot:run
```

这个服务会在本地的 8888 端口启动一个 Spring Boot 应用，提供一个 REST API 来调用 MCP 服务。

## 测试示例

启动所有服务后，您可以通过以下方式测试示例：

### 测试 toUpperCase 功能

发送 HTTP 请求到客户端的 `/client/stream/chat` 端点，询问将文本转换为大写：

```bash
curl "http://localhost:8888/client/stream/chat?query=请将文本 'hello world' 转换为大写"
```

### 测试 getTime 功能

发送 HTTP 请求到客户端的 `/client/stream/chat` 端点，询问特定时区的当前时间：

```bash
curl "http://localhost:8888/client/stream/chat?query=请告诉我 UTC+8 时区的当前时间"
```

## 预期结果

### toUpperCase 功能

当您请求将文本转换为大写时，大模型会调用 toUpperCase 工具，并返回类似以下的响应：

```
我已将文本 'hello world' 转换为大写：HELLO WORLD
```

### getTime 功能

当您请求特定时区的当前时间时，大模型会调用 getTime 工具，并返回类似以下的响应：

```
UTC+8 时区的当前时间是：2023-06-01 12:34:56.789
```

注意：实际返回的时间将是请求时的当前时间。

## 工作原理

1. 客户端接收用户的自然语言请求
2. 大模型理解请求并决定调用适当的工具
3. 大模型通过 MCP 协议调用 Bridge 提供的工具
4. Bridge 将 MCP 请求转换为 gRPC 请求并调用 gRPC 服务
5. gRPC 服务处理请求并返回结果
6. Bridge 将 gRPC 响应转换为 MCP 响应并返回给大模型
7. 大模型将结果整合到自然语言响应中并返回给用户
