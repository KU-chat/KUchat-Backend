package kuchat.server.domain.chatroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.Validator;
import kuchat.server.domain.chatroom.Chatroom;
import kuchat.server.domain.chatroom.dto.*;
import kuchat.server.domain.chatroom.service.ChatroomService;
import kuchat.server.domain.member.service.MemberService;
import kuchat.server.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static kuchat.server.common.response.BaseResponseStatus.EMPTY_CHATROOM;
import static kuchat.server.common.response.BaseResponseStatus.SUCCESS;

@Slf4j
@Tag(name = "Chatroom", description = "채팅방")
@RequiredArgsConstructor
@RequestMapping("/chatroom")
@RestController
public class ChatroomController {

    private final ChatroomService chatroomService;
    private final MessageService messageService;
    private final MemberService memberService;

    @Operation(summary = "채팅방 생성")
    @PostMapping("")
    public ResponseEntity<ChatroomResponse> create(@Validated @RequestBody CreateChatroomRequest request,
                                                   BindingResult bindingResult) {
        log.info("[create] 채팅방 생성 요청 request = {}", request.toString());
        if (request.getMemberIds().isEmpty()) {
            throw new KuchatException(EMPTY_CHATROOM);
        }
        Validator.validateRequest(bindingResult);
        ChatroomResponse response = chatroomService.create(request);

        URI location = ServletUriComponentsBuilder      // 새롭게 생성된 채팅방의 uri를 알려주는 용도
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);     // 201 created
    }

    @Operation(summary = "채팅방 이름으로 채팅방 목록 조회")
    @GetMapping("")
    public ResponseEntity<FindChatroomsResponse> search(@RequestParam("name") String name) {
        log.info("[search] 검색어 : {}", name);
        FindChatroomsResponse response = chatroomService.findChatrooms(name);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "채팅방 이름 변경 (단체 톡방만 가능)")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponseStatus> updateName(@PathVariable("id") Long chatroomId,
                                                         @Validated @RequestBody UpdateChatroomRequest request,
                                                         BindingResult bindingResult) {
        String newName = request.getNewName();
        log.info("[updateName] 채팅방 번호 = {}, 바꿀 이름 = {}", chatroomId, newName);
        Validator.validateRequest(bindingResult);

        chatroomService.updateName(chatroomId, newName);
        return ResponseEntity.ok(SUCCESS);
    }

    @Operation(summary = "채팅 화면으로 들어가기 (최근 20개 톡 가져오기)")
    @GetMapping("/{id}/enter")
    public ResponseEntity<EnterChatroomResponse> enter(@PathVariable("id") Long chatroomId) {
        log.info("{}번 채팅방 화면으로 이동", chatroomId);
        EnterChatroomResponse response = messageService.enter(chatroomId);        // 최근 20개 톡 가져오기
        response.setMemberInfos(SUCCESS, chatroomService.findMembersByChatroomId(chatroomId));
        return ResponseEntity.ok().body(response);
    }
}
