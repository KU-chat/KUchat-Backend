package kuchat.server.common.websocket;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//    private final SubscriptionInterceptor subscriptionInterceptor;

    @Override
    public void configureMessageBroker (MessageBrokerRegistry mqRegistry){
        mqRegistry.enableSimpleBroker("/sub");       // 메세지 구독 요청의 prefix 설정
        mqRegistry.setApplicationDestinationPrefixes("/pub");           // 메세지 발행 요청의 prefix 설정
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        log.info("[registerStompEndpoints] Registering STOMP endpoint at /stomp");
        registry.addEndpoint("/stomp")     // stomp websocket 연결 end point
                .setAllowedOriginPatterns("*");
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration){
//        registration.interceptors(subscriptionInterceptor);       // 인터셉터 등록
//        // 사용자가 웹소켓 연결할 때 & 연결이 끊길 때 추가 기능을 위해 인터셉터 추가 (인증, 세션관리 등)
//    }
}
