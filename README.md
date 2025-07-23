# rpc-llm-bridge-java

rpc-llm-bridge-java 是一个大模型工具调用的框架，可以将 RPC 转换为供大模型使用的工具。通过这个框架，开发者可以轻松地将自己的 RPC 服务集成到大模型的工具调用框架中，实现大模型对各种服务的调用。

[English Documentation](README.en.md)

## 项目概览

rpc-llm-bridge-java 提供了一套完整的解决方案，用于将 RPC 服务转换为大模型可调用的工具。该框架具有以下特点：

- **支持 Spring Boot 自动装配**：轻松集成到 Spring Boot 项目中
- **支持工具自动注册和发现**：自动发现和注册 RPC 服务作为工具
- **支持自定义工具调用参数和返回值格式**：通过 proto 扩展提供灵活的格式定义
- **提供多种集成方式**：支持直接工具调用、MCP 协议集成等多种方式

## 主要模块

- **rpc-llm-bridge-core**：核心接口模块，定义了工具调用以及工具注册和发现的基本接口。
- **rpc-llm-bridge-grpc**：基于 gRPC 的实现模块，继承了 rpc-llm-bridge-core 中定义的接口，提供了基于 gRPC 的框架实现。
- **rpc-llm-bridge-springboot-starter**：Spring Boot 自动配置模块，提供了 Spring Boot 的自动配置和集成，方便在 Spring Boot 项目中使用。主要功能：
  - 自动配置工具回调机制
  - 自动注册和发现工具
  - 与 Spring AI 无缝集成

## 示例项目

### auto-discovery-example

自动发现工具并进行调用的示例。该示例演示了如何使用自动工具发现功能与 gRPC 服务进行集成，实现大模型工具的自动发现和调用。

详细文档请参阅 [auto-discovery-example README](example/auto-discovery-example/README.md)。

主要组件：
- gRPC 服务：提供 toUpperCase 和 getTime 方法
- 自动发现客户端：集成 Spring AI 和自动工具发现功能

工作流程：
1. 客户端启动时自动发现 gRPC 服务提供的工具
2. 客户端接收用户的自然语言请求
3. Spring AI 处理请求并决定调用适当的工具
4. 通过 Tool Callback 机制，Spring AI 调用自动发现的工具
5. 工具通过 gRPC 客户端调用 gRPC 服务
6. 结果通过 Tool Callback 返回给 Spring AI 并最终返回给用户

### tooluse-example

工具调用示例。该示例演示了如何使用 Spring AI 的 Tool Callback 功能与 gRPC 服务进行集成，实现大模型工具调用。

详细文档请参阅 [tooluse-example README](example/tooluse-example/README.md)。

主要组件：
- gRPC 服务：提供 toUpperCase 和 getTime 方法
- Tool Callback 客户端：集成 Spring AI 和 Tool Callback 功能

与自动发现示例的区别：本示例直接使用 Spring AI 的 Tool Callback 机制，需要手动定义和注册工具。

### mcp-example

MCP 调用示例。该示例演示了如何将 gRPC 服务通过 MCP Bridge 转换为 MCP 服务，并使用客户端调用该服务。

详细文档请参阅 [mcp-example README](example/mcp-example/README.md)。

主要组件：
- gRPC 服务：提供 toUpperCase 和 getTime 方法
- MCP Bridge：将 gRPC 服务转换为 MCP 服务
- MCP 客户端：通过大模型调用 MCP 服务

工作流程：
1. 客户端接收用户的自然语言请求
2. 大模型理解请求并决定调用适当的工具
3. 大模型通过 MCP 协议调用 Bridge 提供的工具
4. Bridge 将 MCP 请求转换为 gRPC 请求并调用 gRPC 服务
5. 结果通过 Bridge 返回给大模型并最终返回给用户

## 未来规划

rpc-llm-bridge-java 项目计划在未来实现以下功能：

1. **支持更多 RPC 框架**：除了 gRPC，计划增加对 Dubbo、Thrift 等其他 RPC 框架的支持
2. **增强工具发现机制**：提供更灵活的工具发现和过滤机制
3. **提供更丰富的工具元数据**：支持更详细的工具描述和使用说明
4. **增强安全性**：提供更完善的安全机制，包括认证、授权和访问控制
5. **提供监控和统计功能**：增加对工具调用的监控和统计功能
6. **支持更多大模型平台**：增加对更多大模型平台的支持

## 贡献指南

我们欢迎社区贡献，如果您有兴趣参与项目开发，请遵循以下步骤：

1. Fork 项目仓库
2. 创建您的特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交您的更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建一个 Pull Request

## 许可证

本项目采用 Apache 2.0 许可证 - 详情请参阅 [LICENSE](LICENSE) 文件。
