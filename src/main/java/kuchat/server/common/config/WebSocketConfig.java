package kuchat.server.common.config;


import kuchat.server.domain.message.WebSocketInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketInterceptor webSocketInterceptor;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketInterceptor);
    }

    @Override
    public void configureMessageBroker (MessageBrokerRegistry mqRegistry){

        // enableSimpleBroker : 스프링이 제공하는 인메모리 브로커를 사용하겠다는 의미

        // 이 두 경로가 prefix 에 붙은 경우, messageBroker 가 잡아서 해당 채팅방을 구독하고 있는 클라이언트에게 메시지를 전달해줌
        // /queue 는 1대1 메시징, /topic 은 1대다 메시징에 주로 사용함
//        mqRegistry.enableSimpleBroker("/queue", "/topic");     // stomp websocket 연결 end point
//        mqRegistry.setApplicationDestinationPrefixes("/stomp");     // 클라이언트가 보낸 메시지 경로 맨 앞에 /stomp 가 붙어 있으면 Broker에게 보내짐 (메시지 보낼 때 관련 경로 설정)

        mqRegistry.enableSimpleBroker("/subscribe");
        mqRegistry.setApplicationDestinationPrefixes("/publish");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        log.info("[registerStompEndpoints] Registering STOMP endpoint at /stomp");

        // 클라이언트에서 websocket을 연결할 때 사용할 API 경로를 설정
        registry.addEndpoint("/ws-connect")
                .setAllowedOriginPatterns("*");
    }

}
