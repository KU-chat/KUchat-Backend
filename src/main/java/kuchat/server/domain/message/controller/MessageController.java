package kuchat.server.domain.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.domain.chatroom.Chatroom;
import kuchat.server.domain.chatroom.service.ChatroomService;
import kuchat.server.domain.message.dto.ChatMessage;
import kuchat.server.domain.message.dto.ChatroomJoinRequest;
import kuchat.server.domain.message.dto.MessageResponse;
import kuchat.server.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "Message", description = "메시지")
@RequiredArgsConstructor
@RequestMapping("")
@RestController
public class MessageController {

    private final MessageService messageService;
    private final ChatroomService chatroomService;
//    private final RedisService redisService;

    @Operation(summary = "채팅방에서 사용자가 주고받는 메세지 처리")
    @MessageMapping("/{chatroomId}/message")            // pub/{chatroomId}/message 로 발행된 메세지를 처리한 후
    @SendTo("/sub/{chatroomId}")                        // sub/{chatroomId} 로 전송한다.
    public MessageResponse handleMessage(@DestinationVariable("chatroomId") Long chatroomId, ChatMessage message) {
        log.info("[handleMessage] 들어온 메세지 = {}", message);
        // 채팅 저장 -> (채팅 보내는거 따로 구현 안해도 됨) -> 보낼 메세지 만들어서 return
        Chatroom chatroom = chatroomService.getChatroom(chatroomId);
        return messageService.handleReceivedMessage(chatroom, message);
    }

    @Operation(summary = "채팅방에 멤버 추가 요청 처리")
    @MessageMapping("/join/chatroom/{chatroomId}")          // pub/join/chatroom/{chatroomId} 로 발행된 메세지를 처리한 후
    @SendTo("/sub/{chatroomId}")                            // sub/{chatroomId} 로 전송한다.
    public MessageResponse join(@DestinationVariable("chatroomId") Long chatroomId, ChatroomJoinRequest joinRequest) {
        log.info("[join] join 요청 = {}", joinRequest.toString());
        List<Long> memberIds = joinRequest.getMemberIds();
        return chatroomService.join(chatroomId, memberIds);
    }

    @Operation(summary = "채팅방 나가기 요청 처리")
    @MessageMapping("/leave/chatroom/{chatroomId}")         // pub/leave/chatroom/{chatroomId} 로 발행된 메세지를 처리한 후
    @SendTo("/sub/{chatroomId}")                            // sub/{chatroomId} 로 전송한다.
    public MessageResponse leave(@DestinationVariable("chatroomId") Long chatroomId, ChatMessage message) {
        log.info("[leave] 들어온 메세지 = {}", message.toString());
        Long memberId = message.getSenderId();
        return chatroomService.leave(chatroomId, memberId);
    }
}
