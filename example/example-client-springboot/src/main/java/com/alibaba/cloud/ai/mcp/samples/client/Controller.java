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

@RestController
@RequestMapping("/client")
public class Controller {


    private static final String DEFAULT_PROMPT = "你好，介绍下你自己！";

    private final ChatClient chatClient;

    private final ChatModel chatModel;

    public Controller(ChatModel chatModel, ToolCallbackProvider tools) {

        this.chatModel = chatModel;

        // 构造时，可以设置 ChatClient 的参数
        // {@link org.springframework.ai.chat.client.ChatClient};
        this.chatClient = ChatClient.builder(chatModel)
                // 实现 Chat Memory 的 Advisor
                // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build()
                )
                // 实现 Logger 的 Advisor
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withTopP(0.7)
                                // 设置enable_thinking 为 true，启用思考过程
                                .withEnableThinking(true)
                                .build()
                )
                .defaultToolCallbacks(tools)
                .build();
    }

    @GetMapping("/stream/chat")
    public Flux<String> streamChat(@RequestParam("query") String query,
                                   HttpServletResponse response) {
        // 避免返回乱码
        response.setCharacterEncoding("UTF-8");
        return chatClient.prompt(query)
                .stream()
                .content();
    }

}
