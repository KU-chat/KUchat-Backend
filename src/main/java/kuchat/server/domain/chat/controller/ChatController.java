package kuchat.server.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.common.jwt.argumentResolver.Auth;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.domain.Validator;
import kuchat.server.domain.chat.Chat;
import kuchat.server.domain.chat.dto.CreateChatRequest;
import kuchat.server.domain.chat.service.ChatService;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.message.dto.RecentMessageResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Chat", description = "1:1 채팅방 + 단체 채팅방 모두에 대한 요청을 받음")
@RequiredArgsConstructor
@RequestMapping("/chat")
@RestController
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "채팅방 생성 (1:1 채팅, 그룹 채팅 모두 해당)")
    @PostMapping
    public ResponseEntity<BaseResponse> create(@Auth Member member, @RequestBody @Validated CreateChatRequest request,
                                               BindingResult bindingResult) {
        log.info("[create] id = {} 인 사용자가 {} 와 개인 채팅방 생성 요청", member.getId(), request.toString());
        Validator.validateRequest(bindingResult);
        return ResponseEntity.ok(chatService.create(member, request));
    }

    @Operation(summary = "채팅방 화면으로 들어가기 (채팅방 메시지 조회하기)")
    @GetMapping("/{chatId}")
    public ResponseEntity<RecentMessageResponses> enter(@Auth Member member,
                                              @PathVariable("chatId") Long chatId,
                                              @PageableDefault(
                                                      size = 50,
                                                      sort = "createdDate",
                                                      direction = Sort.Direction.DESC
                                              ) Pageable pageable) {
        Chat chat = chatService.validateEnter(member, chatId);
        RecentMessageResponses responses = chatService.getRecentMessages(chat, pageable);
        return ResponseEntity.ok(responses);
    }

}
