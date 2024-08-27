package kuchat.server.domain.message.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.domain.chatroom.service.ChatroomService;
import kuchat.server.domain.message.dto.ChatMessage;
import kuchat.server.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Message", description = "메시지")
@RequiredArgsConstructor
@RequestMapping("chat")
@RestController
public class MessageController {

    private final ChatroomService chatroomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
//    private final RedisService redisService;


    @MessageMapping("/chatroom")                   // /pub/chatroom 으로 들어오는 메시지를 처리하는 api
    public void send(ChatMessage message) {
        // @Validated 쓰니까 역직렬화 오류 발생해서 사용X

        log.info("[send] senderId={} 가 보낸 메시지를 room={} 에 전송", message.getSenderId(), message.getChatroomId());

        // 지금은 STOMP 작동만 확인하는 중이어서 주석처리
//        if(message.getChatroomId() == null || !chatroomService.existChatroom(Long.parseLong(roomId))){
//            log.info("채팅방 id가 제대로 입력되어 있지 않습니다.");
//            throw new KuchatException(MESSAGE_FORMAT_ERROR);
//        }

        log.info("[send] ChatMessage 객체 = {}", message);
        messagingTemplate.convertAndSend("/sub/chatroom/" + message.getChatroomId(), message);
    }


//    @Operation(summary = "채팅방에서 사용자가 메세지 전송")
//    @MessageMapping("/{chatroomId}/message")            // pub/{chatroomId}/message 로 발행된 메세지를 처리한 후
//    @SendTo("/sub/{chatroomId}")                        // sub/{chatroomId} 로 전송한다.
//    public MessageResponse handleMessage(@DestinationVariable("chatroomId") Long chatroomId,
//                                         @Validated ChatMessage message, BindingResult bindingResult) {
//        log.info("[handleMessage] 들어온 메세지 = {}", message);
//        if(bindingResult.hasErrors()) {
//            String messages = getErrorMessages(bindingResult);
//            log.error("[handleMessage] bindingResult messages = {}", messages);
//            throw new KuchatException(MESSAGE_FORMAT_ERROR, messages);
//        }
//
//        // 채팅 저장 -> (채팅 보내는거 따로 구현 안해도 됨) -> 보낼 메세지 만들어서 return
//        Chatroom chatroom = chatroomService.getChatroom(chatroomId);
//        return messageService.handleReceivedMessage(chatroom, message);
//    }
//
//    @Operation(summary = "채팅방에 멤버 추가")
//    @MessageMapping("/join/chatroom/{chatroomId}")          // pub/join/chatroom/{chatroomId} 로 발행된 메세지를 처리한 후
//    @SendTo("/sub/{chatroomId}")                            // sub/{chatroomId} 로 전송한다.
//    public MessageResponse join(@DestinationVariable("chatroomId") Long chatroomId,
//                                @Validated ChatroomJoinRequest joinRequest,
//                                BindingResult bindingResult) {
//        log.info("[join] join 요청 = {}", joinRequest.toString());
//        if(bindingResult.hasErrors()) {
//            String messages = getErrorMessages(bindingResult);
//            log.error("[join] bindingResult messages = {}", messages);
//            throw new KuchatException(MESSAGE_FORMAT_ERROR, messages);
//        }
//        List<Long> memberIds = joinRequest.getMemberIds();
//        return chatroomService.join(chatroomId, memberIds);
//    }
//
//    @Operation(summary = "채팅방 나가기")
//    @MessageMapping("/leave/chatroom/{chatroomId}")         // pub/leave/chatroom/{chatroomId} 로 발행된 메세지를 처리한 후
//    @SendTo("/sub/{chatroomId}")                            // sub/{chatroomId} 로 전송한다.
//    public MessageResponse leave(@DestinationVariable("chatroomId") Long chatroomId,
//                                 @Validated ChatMessage message, BindingResult bindingResult) {
//        log.info("[leave] 들어온 메세지 = {}", message.toString());
//        if(bindingResult.hasErrors()) {
//            String messages = getErrorMessages(bindingResult);
//            log.error("[leave] bindingResult messages = {}", messages);
//            throw new KuchatException(MESSAGE_FORMAT_ERROR, messages);
//        }
//        Long memberId = message.getSenderId();
//        return chatroomService.leave(chatroomId, memberId);
//    }
//
//    private static String getErrorMessages(BindingResult bindingResult) {
//        return bindingResult.getAllErrors().stream()
//                .map(error -> error.getDefaultMessage())
//                .collect(Collectors.joining("$"));
//    }
}
