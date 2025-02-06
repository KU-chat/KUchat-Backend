package kuchat.server.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.common.jwt.argumentResolver.Auth;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.domain.chat.dto.CreateChatResponse;
import kuchat.server.domain.chat.service.ChatService;
import kuchat.server.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Chat", description = "1:1 채팅방 + 단체 채팅방 전반적인 요청을 받음")
@RequiredArgsConstructor
@RequestMapping("/chat")
@RestController
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "개인 채팅방 생성")
    @PostMapping("/{memberId}")
    public ResponseEntity<CreateChatResponse> create(@Auth Member member, @PathVariable("memberId") Long friendId) {
        log.info("[create] id = {} 인 사용자가 {} 와 개인 채팅방 생성 요청", member.getId(), friendId);
        return chatService.create(member, friendId);
    }

    @Operation(summary = "개인 채팅방 화면으로 들어가기")
    @GetMapping("/{chatId}")
    public ResponseEntity<BaseResponse> enter(@Auth Member member, @PathVariable("chatId") Long chatId){
        return chatService.validateEnter(member, chatId);
    }

}
