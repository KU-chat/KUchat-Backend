package kuchat.server.domain.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.domain.message.dto.ChatMessageRequest;
import kuchat.server.domain.message.dto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Message", description = "메시지")
@RequiredArgsConstructor
@RestController
public class MessageController {

//    private final ChatroomService chatroomService;
//    private final SimpMessageSendingOperations sendingOperations;

    @Operation(summary = "사용자가 발행한 메시지 전송")
    @MessageMapping("/chat.{chatroomId}")                   // /pub/chatroom 으로 들어오는 메시지를 처리하는 api
    @SendTo("/sub/chat.{chatroomId}")
    public ChatMessageResponse sendMessage(ChatMessageRequest request, @DestinationVariable("chatroomId") Long chatroomId) {

        log.info("message request = {}", request.toString());
        log.info("채팅방 번호 = {}", chatroomId);

        return new ChatMessageResponse(request.username(), request.content());
//        sendingOperations.convertAndSend("/topic/chat/room/" + message.getChatroomId(), message);
    }
}
