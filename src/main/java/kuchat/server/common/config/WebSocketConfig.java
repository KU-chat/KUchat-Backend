package kuchat.server.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//    private final WebSocketHandler webSocketHandler;

    @Override
    public void configureMessageBroker (MessageBrokerRegistry mqRegistry){
        mqRegistry.enableSimpleBroker("/sub");
        mqRegistry.setApplicationDestinationPrefixes("/pub");
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws-stomp").setAllowedOrigins("*")
                .withSockJS();
    }
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(webSocketHandler, "/ws/message")      // 서버 세팅 후, ws://도메인/ws/message 에서 테스트할 url 지정
//                .setAllowedOrigins("*");        // 웹소켓 정책에 의해 허용 도메인 지정 (* : 모든 도메인 열어주기)
//    }
}
