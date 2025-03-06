package kuchat.server.domain.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.domain.chat.Chat;
import kuchat.server.domain.chat.service.ChatService;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.service.MemberService;
import kuchat.server.domain.message.dto.MessageRequest;
import kuchat.server.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Tag(name = "Message", description = "메시지")
@RequiredArgsConstructor
//@RestController
@Controller
public class MessageController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MemberService memberService;
    private final ChatService chatService;
    private final MessageService messageService;


    @Operation(summary = "채팅방 구성원이 보낸 메시지 전송")
    @MessageMapping("/chat/{chatroomId}")                   // /pub/chatroom 으로 들어오는 메시지를 처리하는 api
    public void sendMessage(@Payload MessageRequest messageRequest,
                              @DestinationVariable("chatId") Long chatId) {

        log.info("message request = {}", messageRequest.toString());
        log.info("채팅방 번호 = {}", chatId);

        Chat chat = chatService.getChatById(chatId);
        Member sender = memberService.getMemberById(messageRequest.getSenderId());
//        messageService.save(chat, sender, messageRequest);


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
