package kuchat.server.common.config;


import kuchat.server.domain.message.WebSocketInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

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

    /**
     * 메시지 라우팅 설정
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry mqRegistry) {

        // 클라이언트 → 서버 로 오는 요청
        //      @MessageMapping 메서드가 /msg 으로 시작하는 요청을 처리하도록 한다.
        mqRegistry.setApplicationDestinationPrefixes("/ku");

        // 서버 → 클라이언트 로 가는 요청
        //      브로커가 자동으로 메시지를 전송해준다.
        mqRegistry.enableSimpleBroker("/queue", "/topic");      // queue : 개인톡, topic : 단체톡

        // enableSimpleBroker : 스프링이 제공하는 인메모리 브로커를 사용하겠다는 의미
    }

    /**
     * websocket 연결 설정
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("[registerStompEndpoints] Registering STOMP endpoint at /stomp");

        // 클라이언트가 websocket 연결을 맺기 위해 접속해야 하는 http 엔드포인트
        registry.addEndpoint("/ws-connect")
                .setAllowedOrigins("*");
//                .withSockJS();        // 이 옵션 추가하면 오류가 나는 이유가 뭘까?
    }

    /**
     * STOMP websocket 전송 속성
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(32 * 1024);    // 메시지 크기 제한 : 디폴트 64KB 에서 32KB 로 변경
        registry.setTimeToFirstMessage(30 * 1000);      // 클라이언트가 websocket을 연결한 후 30초 내에 STOMP 메시지를 보내야 함.
        // 보내지 않으면 서버가 클라이언트 연결 종료
    }

}
