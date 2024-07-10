package kuchat.server.common.config;

import kuchat.server.domain.message.service.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws/message")      // 서버 세팅 후, ws://도메인/ws/message 에서 테스트할 url 지정
                .setAllowedOrigins("*");        // 웹소켓 정책에 의해 허용 도메인 지정 (* : 모든 도메인 열어주기)
    }
}
