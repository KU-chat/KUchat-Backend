package kuchat.server.domain.message.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventHandler {

    @EventListener
    public void connectHandler(SessionConnectedEvent e){
        // 연결을 성공적으로 마친 경우
        // redis에 세션 연결 정보 저장?
    }

    @EventListener
    public void disconnectHandler(SessionDisconnectEvent e){
        // 연결이 끊어진 경우
    }
}
