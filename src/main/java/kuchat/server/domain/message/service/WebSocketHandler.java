package kuchat.server.domain.message.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.enums.MessageType;
import kuchat.server.domain.message.ChatMessageEvent;
import kuchat.server.domain.message.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static kuchat.server.common.exception.BaseResponse.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<Long, Set<WebSocketSession>> chatroomSessions = new ConcurrentHashMap<>();          // chatroom id - session 매핑
    private final MessageService messageService;

    // 클라이언트가 WebSocket 서버에 접속/연결할 때 호출되는 메서드 (join하는 chatroom에 클라이언트의 세션 추가하기)
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("[afterConnectionEstablished] websocket 서버에 접속을 시도한 클라이언트의 세션 id = {}", session.getId());
        TextMessage welcomeMessage = new TextMessage("web socket 서버 접속에 성공했습니다.");
        sendMessage(session, welcomeMessage, WEBSOCKET_CONNECTION_FAIL);
    }

    // /chatroom/{chatroomId}로 끝나는 uri에서 chatroomId 추출하는 메서드
    private Long extractChatroomId(String path) {
        try {
            // path 형태 : {도메인}/chatroom/{chatroomId}/join
            Long chatroomId = Long.parseLong(path.split("/chatroom")[1]);
            return chatroomId;
        } catch (NumberFormatException e) {
            log.error("uri의 채팅방 id가 올바르지 않습니다. uri = {}", path);
            throw new KuchatException(BaseResponse.MALFORMED_CHATROOM_ID);
        }
    }

    // 클라이언트가 WebSocket 서버와의 연결을 종료할 때 호출되는 메서드 (leave 하는 chatroom에 클라이언트의 세션 제거하기)
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("[afterConnectionClosed] websocket 서버와의 연결을 종료한 클라이언트의 세션 id = {}", session.getId());
        TextMessage farewellMessage = new TextMessage("web socket 서버와의 연결을 종료합니다.");
        sendMessage(session, farewellMessage, WEBSOCKET_CLOSE_FAIL);

    }

    // 클라이언트가 WebSocket 서버로 메시지를 전송할 때 호출되는 메서드
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("[handleTextMessage] 메세지를 전송한 클라이언트의 세션 id = {}, 메세지의 payload = {}", session.getId(), message.getPayload());
        ChatMessage chatMessage = toChatMessage(message);
        if (messageService.handleReceivedMessage(chatMessage)) {
            sendMessageToClients(chatMessage.getChatroomId(), message);
        }
    }

    // 서버 -> 클라이언트 로 전송할 메시지를 처리  (MessageService 에서 생성한 ChatMessage 를 전송)
    // 클라언트가 특정 채팅방에 join 혹은 leave 할 때 event 가 발생됨
    @EventListener
    public void onChatMessageEvent(ChatMessageEvent event) {
        ChatMessage chatMessage = event.getChatMessage();
        TextMessage textMessage = toTextMessage(chatMessage);
        sendMessageToClients(chatMessage.getChatroomId(), textMessage);
    }

    private void sendMessageToClients(Long chatroomId, TextMessage textMessage) {
        log.info("[send] 메시지 : {}", textMessage.getPayload());
        Set<WebSocketSession> sessions = findSessionsByChatroomId(chatroomId);

        if (sessions != null) {
            sessions.stream()
                    .filter(WebSocketSession::isOpen)
                    .forEach(session -> {
                        sendMessage(session, textMessage, MESSAGE_SEND_FAIL);
                    });
        }
    }

    private Set<WebSocketSession> findSessionsByChatroomId(Long chatroomId) {
        return chatroomSessions.get(chatroomId);
    }

    private ChatMessage toChatMessage(TextMessage textMessage) {
        log.info("[toChatMessage] textMessage를 ChatMessage로 바꿔주는 메서드, textMessage = {}", textMessage.getPayload());
        try {
//            return new ChatMessage(textMessage);
            return objectMapper.readValue(textMessage.getPayload(), ChatMessage.class);
        } catch (Exception e) {
            throw new KuchatException(CONVERT_TO_OBJECT_FAIL);
        }
    }

    private TextMessage toTextMessage(ChatMessage chatMessage) {
        try {
            return new TextMessage(objectMapper.writeValueAsString(chatMessage));
        } catch (JsonProcessingException e) {
            throw new KuchatException(CONVERT_TO_JSON_FAIL);
        }
    }

    private static void sendMessage(WebSocketSession session, TextMessage message, BaseResponse response) {
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            throw new KuchatException(response);
        }
    }

}
