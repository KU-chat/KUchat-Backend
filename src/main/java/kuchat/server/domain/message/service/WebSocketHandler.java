package kuchat.server.domain.message.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.redis.RedisController;
import kuchat.server.domain.message.ChatMessageEvent;
import kuchat.server.domain.message.dto.ChatMessage;
import kuchat.server.domain.message.dto.ChatroomJoinRequest;
import kuchat.server.domain.message.dto.ChatroomLeaveRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static kuchat.server.common.exception.BaseResponse.*;
import static kuchat.server.domain.enums.MessageType.JOIN;
import static kuchat.server.domain.enums.MessageType.LEAVE;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final MessageService messageService;
    private final RedisController redisController;

    private ConcurrentHashMap<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();         // member id - webSocketSession
    private ConcurrentHashMap<Long, Set<WebSocketSession>> chatroomSessions = new ConcurrentHashMap<>();          // chatroom id - session 매핑

    // 클라이언트가 WebSocket 서버에 접속/연결할 때 호출되는 메서드
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String query = session.getUri().getQuery();     // ws://localhost:9000/ws/message?memberId={id}
        Map<String, String> queryParams = parseQueryParam(query);
        Long memberId = Long.parseLong(queryParams.get("memberId"));
        sessions.put(memberId, session);
        redisController.putMemberSession(memberId, session.getId());
        log.info("[afterConnectionEstablished] websocket 서버에 접속을 시도한 클라이언트의 세션 id = {}, member id = {}",
                session.getId(), memberId);
        TextMessage welcomeMessage = new TextMessage("web socket 서버 접속에 성공했습니다.");
        sendMessage(session, welcomeMessage, WEBSOCKET_CONNECTION_FAIL);
    }

    private Map<String, String> parseQueryParam(String query) {
        Map<String, String> queryParams = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx != -1) {     // '='이 포함된 경우
                    String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                    queryParams.put(key, value);
                } else {            // '=' 없이 키만 있는 경우
                    queryParams.put(URLDecoder.decode(pair, StandardCharsets.UTF_8), null);
                }
            }
        }
        return queryParams;
    }

    // 클라이언트가 WebSocket 서버와의 연결을 종료할 때 호출되는 메서드 (leave 하는 chatroom에 클라이언트의 세션 제거하기)
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String query = session.getUri().getQuery();     // ws://localhost:9000/ws/message?memberId={id}
        Map<String, String> queryParams = parseQueryParam(query);
        Long memberId = Long.parseLong(queryParams.get("memberId"));
        log.info("[afterConnectionClosed] websocket 서버와의 연결을 종료한 클라이언트의 세션 id = {}, member id = {}",
                session.getId(), memberId);
        TextMessage farewellMessage = new TextMessage("web socket 서버와의 연결을 종료합니다.");
        sendMessage(session, farewellMessage, WEBSOCKET_CLOSE_FAIL);
    }

    // 클라이언트가 WebSocket 서버로 메시지를 전송할 때 호출되는 메서드
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("[handleTextMessage] 메세지를 전송한 클라이언트의 세션 id = {}, 메세지의 payload = {}", session.getId(), message.getPayload());
        ChatMessage chatMessage = toChatMessage(message);
        log.info("[toChatMessage] textMessage 를 chatMessage 로 바꾸는데 성공. chatMessage = {}", chatMessage.toString());

        if (messageService.handleReceivedMessage(chatMessage)) {
            sendMessageToClients(chatMessage.getChatroomId(), message);
        }
    }

    // 서버 -> 클라이언트 로 전송할 메시지를 처리  (MessageService 에서 생성한 ChatMessage 를 전송)
    // 클라언트가 특정 채팅방에 join 혹은 leave 할 때 event 가 발생됨
    @EventListener
    public void onChatMessageEvent(ChatMessageEvent event) {
        ChatMessage chatMessage = event.getChatMessage();
        if (chatMessage.getMessageType() == JOIN) {
            ChatroomJoinRequest joinRequest = (ChatroomJoinRequest) chatMessage;
            WebSocketSession webSocketSession = getSession(joinRequest.getMemberId());
            log.info("[onChatMessageEvent : JOIN] 세션을 추가할 멤버의 id = {}, session id = {}",
                    joinRequest.getMemberId(), webSocketSession.getId());
            Set<WebSocketSession> chatroomSessions = getSessionSet(joinRequest.getChatroomId());
            log.info("[onChatMessageEvent : JOIN] 세션을 추가하기 전 set = {}", chatroomSessions.toString());
            chatroomSessions.add(webSocketSession);
            log.info("[onChatMessageEvent : JOIN] 세션을 추가한 후 set = {}", chatroomSessions.toString());

            redisController.putChatroomSession(chatMessage.getChatroomId(),
                    chatMessage.getSenderId(), webSocketSession.getId());
            return;
        }

        if (chatMessage.getMessageType() == LEAVE) {
            ChatroomLeaveRequest joinRequest = (ChatroomLeaveRequest) chatMessage;
            WebSocketSession webSocketSession = getSession(joinRequest.getMemberId());
            Set<WebSocketSession> chatroomSessions = this.chatroomSessions.get(joinRequest.getChatroomId());
            chatroomSessions.remove(webSocketSession);
            redisController.deleteChatroomSession(joinRequest.getChatroomId(), joinRequest.getSenderId());
        }

        if (chatMessage.getText() == null) {
            log.info("[onChatMessageEvent] 메세지의 payload 가 비어있어 전송하지 않음. chatMessage = {}", chatMessage.toString());
            return;
        }

        TextMessage textMessage = toTextMessage(chatMessage);
        sendMessageToClients(chatMessage.getChatroomId(), textMessage);
    }

    private WebSocketSession getSession(Long memberId) {
        log.info("[getSession] member id로 web socket session 찾기. member id  = {}", memberId);
        WebSocketSession webSocketSession = sessions.get(memberId);
        log.info("[getSession] member id로 web socket session 찾기. web socket session id = {}", webSocketSession.getId());
        if (webSocketSession == null) {
            throw new KuchatException(NOT_FOUND_SESSION);
        }
        return webSocketSession;
    }

    private void sendMessageToClients(Long chatroomId, TextMessage textMessage) {
        log.info("[sendMessageToClients] 메시지 = {}", textMessage.getPayload());
        Set<WebSocketSession> sessions = getSessionSet(chatroomId);

        sessions.stream()
                .filter(WebSocketSession::isOpen)
                .forEach(session -> {
                    sendMessage(session, textMessage, MESSAGE_SEND_FAIL);
                });

    }

    private Set<WebSocketSession> getSessionSet(Long chatroomId) {
        return chatroomSessions.computeIfAbsent(chatroomId, key -> ConcurrentHashMap.newKeySet());
    }

    private ChatMessage toChatMessage(TextMessage textMessage) {
        log.info("[toChatMessage] textMessage를 ChatMessage로 바꿔주는 메서드, textMessage = {}", textMessage.getPayload());
        try {
            return objectMapper.readValue(textMessage.getPayload(), ChatMessage.class);
        } catch (Exception e) {
            log.info("[toChatMessage] textMessage 를 chatMessage 로 바꾸는데 실패함");
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
        } catch (IllegalStateException e){
            throw new KuchatException(WEBSOCKET_DISCONNECT);
        }
    }

}
