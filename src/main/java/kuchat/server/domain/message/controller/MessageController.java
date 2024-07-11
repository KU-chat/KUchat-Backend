package kuchat.server.domain.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.common.redis.RedisPublisher;
import kuchat.server.common.redis.RedisService;
import kuchat.server.domain.chatroom.service.ChatroomService;
import kuchat.server.domain.enums.MessageType;
import kuchat.server.domain.message.dto.ChatMessage;
import kuchat.server.domain.message.dto.ChatroomJoinRequest;
import kuchat.server.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Message", description = "메시지")
@RequiredArgsConstructor
@RequestMapping("/message")
@RestController
public class MessageController {

    private final MessageService messageService;
    private final ChatroomService chatroomService;

    @Operation(summary = "/pub/message 으로 들어오는 메세지 처리")
    @MessageMapping("/message")
    public void handleMessage(ChatMessage message){
        if(MessageType.JOIN == message.getMessageType()){
            ChatroomJoinRequest joinRequest = (ChatroomJoinRequest) message;
             chatroomService.join(joinRequest.getChatroomId(), joinRequest.getMemberIds());
             return;
        }
        ChannelTopic topic = chatroomService.getTopic(message.getChatroomId());
        messageService.handleReceivedMessage(topic, message);
    }

}
