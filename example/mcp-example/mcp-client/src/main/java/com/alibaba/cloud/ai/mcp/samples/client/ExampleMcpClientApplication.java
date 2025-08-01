/*
 * Copyright 2025-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author brianxiadong
 */
package com.alibaba.cloud.ai.mcp.samples.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the MCP client example. This application demonstrates how to use the
 * MCP client to communicate with AI models through the MCP Gateway.
 */
@SpringBootApplication(exclude = {
        org.springframework.ai.mcp.client.autoconfigure.SseHttpClientTransportAutoConfiguration.class
})
public class ExampleMcpClientApplication {

    /**
     * Entry point for the MCP client application.
     */
    public static void main(String[] args) {
        SpringApplication.run(ExampleMcpClientApplication.class, args);
    }
}
