package io.masterkun.example;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * REST controller that handles client requests for the tool callback example. Provides endpoints
 * for streaming chat interactions with LLM using tool callbacks.
 */
@RestController
@RequestMapping("/client")
public class ExampleController {

    private final ChatClient chatClient;
    private final ToolCallbackProvider provider;

    /**
     * Constructor that initializes the chat client and tool callback provider.
     */
    public ExampleController(ChatModel chatModel, ToolCallbackProvider provider) {
        this.chatClient = ChatClient.create(chatModel);
        this.provider = provider;
    }

    /**
     * Handles streaming chat requests by processing the query through the chat client with tool
     * callbacks.
     */
    @GetMapping("/stream/chat")
    public Flux<String> streamChat(@RequestParam("query") String query,
                                   HttpServletResponse response) {
        // Set character encoding to avoid garbled text
        response.setCharacterEncoding("UTF-8");
        return chatClient.prompt(query)
                .toolCallbacks(provider)
                .toolContext(Map.of("test", "value"))
                .advisors(new SimpleLoggerAdvisor())
                .stream()
                .content();
    }
}
