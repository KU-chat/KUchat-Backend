package kuchat.server.common.websocket;

import kuchat.server.domain.chatroom.service.ChatroomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.ChannelInterceptor;

// 메세지 요청, 응답을 가로채 로직을 수행하는 필터 역할
@Slf4j
@RequiredArgsConstructor
public class SubscriptionInterceptor implements ChannelInterceptor {

    private final ChatroomService chatroomService;

    // 1. 웹소켓 연결 요청 헤더에 authorization을 추가, 토큰값 저장
    // authorization 을 통해 member 검정, 라인 정
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//        if (accessor == null) {
//            throw new KuchatException(STOMP_ACCESSOR_NULL);
//        }
//        if (!isAuthorized(accessor)) {
//            throw new KuchatException(ACCESS_DENIED);
//        }
//    }

//    private boolean isAuthorized(StompHeaderAccessor accessor) {
//
//    }

    // jwt 토큰의 인증정보를 기반으로 구독 요청이 승인되어야 함.

    // jwt 토큰의 인증정보를 기반으로 메세지 전송이 승인되어야 함.
}
