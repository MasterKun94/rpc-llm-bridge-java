# gRPC MCP Bridge

gRPC MCP Bridge 是一个用于将 gRPC 服务转换为 MCP（Model-Client Protocol）服务的工具，使大模型能够通过 MCP 协议调用 gRPC 服务。该项目同时支持 Spring Boot 自动装配功能，用户可以通过注解的方式快速集成 MCP 服务。

## 项目概述

在大模型应用开发中，我们常常需要让大模型调用外部工具或服务来扩展其能力。MCP 协议是一种专为大模型设计的工具调用协议，而 gRPC 是一种高效的远程过程调用框架。本项目旨在搭建 gRPC 和 MCP 之间的桥梁，让开发者能够轻松地将现有的 gRPC 服务暴露为大模型可调用的 MCP 工具。

## 主要组件

项目由以下主要组件组成：

1. **mcp-bridge-grpc-core**：核心模块，提供 gRPC 到 MCP 的转换功能
   - GrpcBridgeToolDefinition：定义 gRPC 方法作为 MCP 工具的元数据
   - GrpcBridgeToolCallback：处理基于工具输入的 gRPC 方法调用
   - GrpcBridgeToolResultConverter：将 protobuf 消息对象转换为格式化字符串

2. **mcp-bridge-grpc-springboot-starter**：Spring Boot 自动装配模块，提供注解支持
   - BridgeToolCallbackAutoConfigure：自动检测并注册 BridgeToolCallback 实例
   - 提供适配器类，桥接 MCP 和 Spring AI 组件

3. **示例应用**：
   - example-grpc-service：示例 gRPC 服务
   - example-grpc-bridge-springboot：示例 MCP Bridge 应用
   - example-client-springboot：示例 MCP 客户端应用

## 使用方法

### 使用 Spring Boot 自动装配

1. 添加依赖：

```xml
<dependency>
    <groupId>io.masterkun.ai</groupId>
    <artifactId>mcp-bridge-grpc-springboot-starter</artifactId>
    <version>${version}</version>
</dependency>
```

2. 创建 gRPC 通道：

```java
@Bean
public ManagedChannel grpcChannel() {
    return ManagedChannelBuilder.forAddress("localhost", 8080)
            .usePlaintext()
            .build();
}
```

3. 注册 gRPC 方法作为 MCP 工具：

```java
@Bean
public BridgeToolCallback<?> myGrpcMethod(ManagedChannel channel) {
    return new GrpcBridgeToolCallback<>(MyServiceGrpc.getMyMethodMethod(), channel);
}
```

### 不使用 Spring Boot

1. 添加依赖：

```xml
<dependency>
    <groupId>io.masterkun.ai</groupId>
    <artifactId>mcp-bridge-grpc-core</artifactId>
    <version>${version}</version>
</dependency>
```

2. 手动创建和注册工具：

```java
ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
        .usePlaintext()
        .build();

BridgeToolCallback<?> callback = new GrpcBridgeToolCallback<>(
        MyServiceGrpc.getMyMethodMethod(), channel);

// 注册到您的 MCP 服务中
```

## 示例场景

项目包含一个完整的示例，演示如何将 gRPC 服务通过 MCP Bridge 转换为 MCP 服务，并使用客户端调用该服务：

1. **example-grpc-service**：提供两个方法的简单 gRPC 服务
   - `toUpperCase`：将字符串转换为大写
   - `getTime`：获取指定时区的当前时间

2. **example-grpc-bridge-springboot**：将 gRPC 服务转换为 MCP 服务的 Spring Boot 应用

3. **example-client-springboot**：通过大模型调用 MCP 服务的 Spring Boot 应用

详细的示例使用说明请参考 [example/README.md](example/README.md)。

## 工作原理

1. 客户端接收用户的自然语言请求
2. 大模型理解请求并决定调用适当的工具
3. 大模型通过 MCP 协议调用 Bridge 提供的工具
4. Bridge 将 MCP 请求转换为 gRPC 请求并调用 gRPC 服务
5. gRPC 服务处理请求并返回结果
6. Bridge 将 gRPC 响应转换为 MCP 响应并返回给大模型
7. 大模型将结果整合到自然语言响应中并返回给用户

## 安装与设置

1. 克隆仓库：

```bash
git clone https://github.com/yourusername/mcp-bridge-grpc.git
cd mcp-bridge-grpc
```

2. 构建项目：

```bash
mvn clean install
```

3. 在您的项目中添加依赖（参见"使用方法"部分）
