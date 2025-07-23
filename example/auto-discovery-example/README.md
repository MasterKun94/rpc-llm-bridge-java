# gRPC 自动工具发现示例

本目录包含了一个完整的示例，演示如何使用自动工具发现功能与 gRPC 服务进行集成，实现大模型工具的自动发现和调用。

## 组件概述

示例由两个主要组件组成：

1. **auto-discovery-example-grpc-service**：一个简单的 gRPC 服务，提供两个方法：
   - `toUpperCase`：将字符串转换为大写
   - `getTime`：获取指定时区的当前时间

2. **auto-discovery-example-caller-service**：一个 Spring Boot 应用，集成了 Spring AI 和自动工具发现功能，允许大模型通过自然语言调用自动发现的 gRPC 服务。

## 启动顺序

请按照以下顺序启动各个组件：

### 1. 启动 gRPC 服务

```bash
cd auto-discovery-example-grpc-service
mvn spring-boot:run
```

启动成功后，控制台会显示：`Example GRPC server started`

这个服务会在本地的 8080 端口启动一个 gRPC 服务器，并启用 gRPC 反射服务，使客户端能够自动发现服务定义。

### 2. 启动自动发现客户端

在启动客户端之前，请确保设置了 DashScope API 密钥环境变量：

```bash
export DASHSCOPE_API_KEY=your_api_key_here
```

然后启动客户端服务：

```bash
cd auto-discovery-example-caller-service
mvn spring-boot:run
```

这个服务会在本地的 8888 端口启动一个 Spring Boot 应用，提供一个 REST API 来处理自然语言请求。

## 测试示例

启动所有服务后，您可以通过以下方式测试示例：

### 测试 toUpperCase 功能

发送 HTTP 请求到客户端的 `/client/stream/chat` 端点，询问将文本转换为大写：

```bash
curl "http://localhost:8888/client/stream/chat?query=把'test'转换为大写"
```

### 测试 getTime 功能

发送 HTTP 请求到客户端的 `/client/stream/chat` 端点，询问特定地区的当前时间：

```bash
curl "http://localhost:8888/client/stream/chat?query=美国华盛顿当前时间是多少"
```

## 预期结果

### toUpperCase 功能

当您请求将文本转换为大写时，大模型会调用 toUpperCase 工具，并返回类似以下的响应：

```
我已将文本 'test' 转换为大写：TEST
```

### getTime 功能

当您请求特定地区的当前时间时，大模型会调用 getTime 工具，并返回类似以下的响应：

```
美国华盛顿的当前时间是：2023-06-01 12:34:56.789
```

注意：实际返回的时间将是请求时的当前时间。

## 工作原理

1. 客户端启动时自动发现 gRPC 服务提供的工具
2. 客户端接收用户的自然语言请求
3. Spring AI 处理请求并决定调用适当的工具
4. 通过 Tool Callback 机制，Spring AI 调用自动发现的工具
5. 工具通过 gRPC 客户端调用 gRPC 服务
6. gRPC 服务处理请求并返回结果
7. 结果通过 Tool Callback 返回给 Spring AI
8. Spring AI 将结果整合到自然语言响应中并返回给用户

## 与其他示例的区别

与 tooluse-example 不同，本示例使用自动工具发现功能，无需手动定义和注册工具。系统会在启动时自动发现 gRPC 服务提供的工具，并将其注册到工具注册表中。
