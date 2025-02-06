package kuchat.server.domain.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.domain.message.dto.ChatInitResponse;
import kuchat.server.domain.message.dto.ChatMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Tag(name = "Message", description = "메시지")
@RequiredArgsConstructor
//@RestController
@Controller
public class MessageController {

//    private final ChatroomService chatroomService;
//    private final SimpMessageSendingOperations sendingOperations;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Operation(summary = "사용자가 발행한 메시지 전송")
    @MessageMapping("/chat/{chatroomId}")                   // /pub/chatroom 으로 들어오는 메시지를 처리하는 api
    @SendTo("/topic/chat/{chatroomId}")
    public String sendMessage(@Payload ChatMessageRequest request,
                              @DestinationVariable("chatroomId") Long chatroomId) {

        log.info("message request = {}", request.toString());
        log.info("채팅방 번호 = {}", chatroomId);

        String sender = request.getSender();
        String receiver = request.getReceiver();
        String message = request.getContent();

        simpMessagingTemplate.convertAndSendToUser(sender, "/queue/chat", request);
        simpMessagingTemplate.convertAndSendToUser(receiver, "/queue/chat", request);
//        return new ChatMessageResponse(request.username(), request.content());
//        sendingOperations.convertAndSend("/topic/chat/room/" + message.getChatroomId(), message);
        return "[" + getTimestamp() + ": " + request.toString();
    }

    private String getTimestamp() {
        return new SimpleDateFormat("yyyy/MM/dd h:mm:ss a").format(new Date());
    }

    @Operation(summary = "사용자의 구독 요청")
    @SubscribeMapping("/chat/init")
    @SendTo("/topic/")
    public ChatInitResponse sendInitialData() {
        return new ChatInitResponse("Welcome!", List.of("User1", "User2"));
    }
}
