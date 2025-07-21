package com.alibaba.cloud.ai.mcp.samples.client;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * REST controller that provides an endpoint for interacting with an AI model.
 * This controller demonstrates how to use the MCP client to communicate with the AI model
 * and how to configure the ChatClient with various options.
 */
@RestController
@RequestMapping("/client")
public class Controller {

    private final ChatClient chatClient;

    private final ChatModel chatModel;

    /**
     * Constructor that configures the ChatClient with various options.
     *
     * @param chatModel The AI chat model to use
     * @param tools The tool callbacks provider for MCP tools
     */
    public Controller(ChatModel chatModel, ToolCallbackProvider tools) {

        this.chatModel = chatModel;

        // Configure the ChatClient with various options
        this.chatClient = ChatClient.builder(chatModel)
                // Add chat memory advisor for maintaining conversation context
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build()
                )
                // Add logger advisor for logging
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                // Configure chat model options
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withTopP(0.7)
                                // Enable thinking process
                                .withEnableThinking(true)
                                .build()
                )
                // Register MCP tool callbacks
                .defaultToolCallbacks(tools)
                .build();
    }

    /**
     * Endpoint for streaming chat responses from the AI model.
     *
     * @param query The user's query to send to the AI model
     * @param response The HTTP response object
     * @return A stream of text responses from the AI model
     */
    @GetMapping("/stream/chat")
    public Flux<String> streamChat(@RequestParam("query") String query,
                                   HttpServletResponse response) {
        // Set character encoding to avoid garbled text
        response.setCharacterEncoding("UTF-8");
        return chatClient.prompt(query)
                .stream()
                .content();
    }

}
