package kuchat.server.common.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.chatroom.service.ChatroomService;
import kuchat.server.domain.enums.MessageType;
import kuchat.server.domain.message.ChatMessageEvent;
import kuchat.server.domain.message.dto.ChatMessage;
import kuchat.server.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static kuchat.server.common.exception.BaseResponse.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;

    private final Set<WebSocketSession> sessions = new HashSet<>();         // 현재 연결된 세션들
    private final ConcurrentHashMap<Long, Set<WebSocketSession>> chatroomSessions = new ConcurrentHashMap<>();          // chatroom id - session 매핑
    private final MessageService messageService;
    private final ChatroomService chatroomService;

    // 사용자가 웹소켓 서버에 접속할 때 동작을 구현 (join하는 chatroom에 클라이언트의 세션 추가하기)
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String path = session.getUri().toString();
        String tmpPath = path.replace("/join", "");     // path 형태 : {도메인}/chatroom/{chatroomId}
        Long chatroomId = extractChatroomId(tmpPath);
        chatroomSessions.get(chatroomId).add(session);
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

    // 사용자의 웹소켓 서버 접속이 끝났을 때 동작을 구현 (leave 하는 chatroom에 클라이언트의 세션 제거하기)
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String path = session.getUri().toString();          // 멤버 나가기 uri : /chatroom/{chatroomId}/memberId/{memberId}/leave
        String tmpPath = path.split("/memberId")[0];        // tmpPath 형태 : /chatroom/{chatroomId}
        Long chatroomId = extractChatroomId(tmpPath);
        chatroomSessions.get(chatroomId).remove(session);
    }

    // 웹소켓 서버가 클라이언트의 메세지를 받을 때 동작을 구현
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String sessionId = session.getId();        // 메세지를 보낸 사람의 세션 아이디
        String text = message.getPayload();
        ChatMessage chatMessage = toChatMessage(message);
        log.info("[handleTextMessage] 받은 메시지의 세션 id = {}, 메시지의 payload = {}", sessionId, text);
        messageService.handleReceivedMessage(chatMessage);
    }

    private ChatMessage toChatMessage(TextMessage message) {
        try {
            return objectMapper.readValue(message.getPayload(), ChatMessage.class);
        } catch (IOException e) {
            throw new KuchatException(CONVERT_TO_OBJECT_FAIL);
        }
    }

    // 서버 -> 클라이언트    로 전송할 메시지 (MessageService에서 생성한 ChatMessage를 전송)
    @EventListener
    public void onChatMessageEvent(ChatMessageEvent event) {
        ChatMessage chatMessage = event.getChatMessage();
        TextMessage textMessage = toTextMessage(chatMessage);
        log.info("[send] 메시지 : {}", chatMessage.toString());
        Set<WebSocketSession> sessions = chatroomSessions.get(chatMessage.getSenderId());

        if (sessions != null) {
            sessions.stream()
                    .filter(WebSocketSession::isOpen)
                    .forEach(session -> {
                        sendMessage(session, textMessage);
                    });
        }
    }

    private TextMessage toTextMessage(ChatMessage chatMessage) {
        try {
            return new TextMessage(objectMapper.writeValueAsString(chatMessage));
        } catch (JsonProcessingException e) {
            throw new KuchatException(CONVERT_TO_JSON_FAIL);
        }
    }

    private static void sendMessage(WebSocketSession session, TextMessage message) {
        try{
            session.sendMessage(message);
        } catch (IOException e) {
            throw new KuchatException(MESSAGE_SEND_FAIL);
        }
    }

}
