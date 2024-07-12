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

import java.util.List;

@Slf4j
@Tag(name = "Message", description = "메시지")
@RequiredArgsConstructor
@RequestMapping("")
@RestController
public class MessageController {

    private final MessageService messageService;
    private final ChatroomService chatroomService;
    private final RedisService redisService;

    @Operation(summary = "/pub/message 으로 들어오는 메세지 처리")
    @MessageMapping("/message")
    public void handleMessage(ChatMessage message){
        log.info("[handleMessage] 들어온 메세지 = {}", message);
        ChannelTopic topic = redisService.getTopic(message.getChatroomId());
        messageService.handleReceivedMessage(topic, message);
    }

    @MessageMapping("/join")
    public void join(ChatroomJoinRequest joinRequest){
        Long chatroomId = joinRequest.getChatroomId();
        List<Long> memberIds = joinRequest.getMemberIds();
        redisService.subscribeTopic(chatroomId, memberIds);
        chatroomService.join(chatroomId, memberIds);
    }

    @MessageMapping("/leave")
    public void leave(ChatMessage message){
        Long chatroomId = message.getChatroomId();
        Long memberId = message.getSenderId();
        redisService.cancelSubscribe(chatroomId, memberId);
        chatroomService.leave(chatroomId, memberId);
    }
}
