package io.masterkun.example;

import io.masterkun.ai.ProxyToolDiscovery;
import io.masterkun.ai.registry.BridgeToolDiscovery;
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

/**
 * 控制器类，提供聊天接口并使用自动发现的工具。
 */
@RestController
@RequestMapping("/client")
public class ExampleController {

    private final ChatClient chatClient;
    private final ProxyToolDiscovery discovery;

    /**
     * 构造函数，初始化聊天客户端和工具发现服务。
     */
    public ExampleController(ChatModel chatModel, ProxyToolDiscovery discovery) {
        this.chatClient = ChatClient.create(chatModel);
        this.discovery = discovery;
    }

    /**
     * 提供流式聊天接口，使用自动发现的工具处理查询。
     */
    @GetMapping("/stream/chat")
    public Flux<String> streamChat(@RequestParam("query") String query,
                                   HttpServletResponse response) {
        // Set character encoding to avoid garbled text
        response.setCharacterEncoding("UTF-8");
        return chatClient.prompt(query)
                .toolCallbacks(discovery.findToolCallbacks())
                .advisors(new SimpleLoggerAdvisor())
                .stream()
                .content();
    }
}
