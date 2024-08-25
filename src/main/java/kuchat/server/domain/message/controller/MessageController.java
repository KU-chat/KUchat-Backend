package kuchat.server.domain.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.common.exception.KuchatException;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static kuchat.server.common.exception.BaseResponse.INFO_BAD_REQUEST;
import static kuchat.server.common.exception.BaseResponse.MESSAGE_FORMAT_ERROR;

@Slf4j
@Tag(name = "Message", description = "메시지")
@RequiredArgsConstructor
@RequestMapping("")
@RestController
public class MessageController {

    private final MessageService messageService;
    private final ChatroomService chatroomService;
//    private final RedisService redisService;

    @Operation(summary = "채팅방에서 사용자가 메세지 전송")
    @MessageMapping("/{chatroomId}/message")            // pub/{chatroomId}/message 로 발행된 메세지를 처리한 후
    @SendTo("/sub/{chatroomId}")                        // sub/{chatroomId} 로 전송한다.
    public MessageResponse handleMessage(@DestinationVariable("chatroomId") Long chatroomId,
                                         @Validated ChatMessage message, BindingResult bindingResult) {
        log.info("[handleMessage] 들어온 메세지 = {}", message);
        if(bindingResult.hasErrors()) {
            String messages = getErrorMessages(bindingResult);
            log.error("[handleMessage] bindingResult messages = {}", messages);
            throw new KuchatException(MESSAGE_FORMAT_ERROR, messages);
        }

        // 채팅 저장 -> (채팅 보내는거 따로 구현 안해도 됨) -> 보낼 메세지 만들어서 return
        Chatroom chatroom = chatroomService.getChatroom(chatroomId);
        return messageService.handleReceivedMessage(chatroom, message);
    }

    @Operation(summary = "채팅방에 멤버 추가")
    @MessageMapping("/join/chatroom/{chatroomId}")          // pub/join/chatroom/{chatroomId} 로 발행된 메세지를 처리한 후
    @SendTo("/sub/{chatroomId}")                            // sub/{chatroomId} 로 전송한다.
    public MessageResponse join(@DestinationVariable("chatroomId") Long chatroomId,
                                @Validated ChatroomJoinRequest joinRequest,
                                BindingResult bindingResult) {
        log.info("[join] join 요청 = {}", joinRequest.toString());
        if(bindingResult.hasErrors()) {
            String messages = getErrorMessages(bindingResult);
            log.error("[join] bindingResult messages = {}", messages);
            throw new KuchatException(MESSAGE_FORMAT_ERROR, messages);
        }
        List<Long> memberIds = joinRequest.getMemberIds();
        return chatroomService.join(chatroomId, memberIds);
    }

    @Operation(summary = "채팅방 나가기")
    @MessageMapping("/leave/chatroom/{chatroomId}")         // pub/leave/chatroom/{chatroomId} 로 발행된 메세지를 처리한 후
    @SendTo("/sub/{chatroomId}")                            // sub/{chatroomId} 로 전송한다.
    public MessageResponse leave(@DestinationVariable("chatroomId") Long chatroomId,
                                 @Validated ChatMessage message, BindingResult bindingResult) {
        log.info("[leave] 들어온 메세지 = {}", message.toString());
        if(bindingResult.hasErrors()) {
            String messages = getErrorMessages(bindingResult);
            log.error("[leave] bindingResult messages = {}", messages);
            throw new KuchatException(MESSAGE_FORMAT_ERROR, messages);
        }
        Long memberId = message.getSenderId();
        return chatroomService.leave(chatroomId, memberId);
    }

    private static String getErrorMessages(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("$"));
    }
}
