package kuchat.server.common.config;


import kuchat.server.common.websocket.SubscriptionInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//    private final WebSocketHandler webSocketHandler;

    @Override
    public void configureMessageBroker (MessageBrokerRegistry mqRegistry){
        mqRegistry.enableSimpleBroker("/sub");       // 메세지 구독 요청의 prefix 설정
        mqRegistry.setApplicationDestinationPrefixes("/pub");           // 메세지 발행 요청의 prefix 설정
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        log.info("[registerStompEndpoints] Registering STOMP endpoint at /ws");
        registry.addEndpoint("/ws-connect")     // stomp websocket 연결 end point
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration){
//        registration.interceptors(new SubscriptionInterceptor());       // 인터셉터 등록
    }
}
