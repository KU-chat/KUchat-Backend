package kuchat.server.domain.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.domain.chat.Chat;
import kuchat.server.domain.chat.service.ChatService;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.service.MemberService;
import kuchat.server.domain.message.dto.MessageRequest;
import kuchat.server.domain.message.dto.MessageResponse;
import kuchat.server.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Message", description = "메시지")
@RequiredArgsConstructor
//@RequestMapping("/message")     // HTTP API 에만 적용된다. (WS에는 적용X)
@RestController
public class MessageController {

    private final MemberService memberService;
    private final ChatService chatService;
    private final MessageService messageService;


//    @Operation(summary = "채팅방 구성원이 보낸 메시지 전송")
    @MessageMapping("/message")
    public void createMessage(@Payload MessageRequest messageRequest) {
        log.info("📩 [sendMessage] : STOMP 메시지 수신 messageRequest = {}", messageRequest);

        Chat chat = chatService.getChatById(messageRequest.getChatId());
        Member sender = memberService.getMemberById(messageRequest.getSenderId());
        MessageResponse response = messageService.save(messageRequest, chat, sender);
        messageService.broadcast(response);

        /** TODO. MessageService 에서 갠톡인지 단톡인지 구분해서 처리
         * 1. Chat 조회
         * 2. Message 엔티티 생성
         * 3. Message 엔티티 저장 : messageService 를 통해 메시지를 MongoDB에 저장
         * 4. 갠톡, 단톡 구분해서 라우팅
         *      갠톡 : simpMessagingTemplate을 사용하여 한명씩 직접 라우팅 (sender, receiver 에게 총 2번 보내야함)
         *      단톡 : simpMessagingTemplate를 사용하여 chatId 를 통해 한번에 라우팅
         */
    }

}
