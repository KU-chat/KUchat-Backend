package kuchat.server.domain.chatroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.domain.chatroom.dto.*;
import kuchat.server.domain.chatroom.service.ChatroomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static kuchat.server.common.exception.BaseResponse.CHATROOM_SUCCESS;

@Slf4j
@Tag(name = "Chatroom", description = "채팅방")
@RequiredArgsConstructor
@RequestMapping("/chatroom")
@RestController
public class ChatroomController {

    private final ChatroomService chatroomService;

    @Operation(summary = "채팅방 생성")
    @PostMapping("")
    public ResponseEntity<ChatroomResponse> createChatroom(@RequestBody CreateChatroomRequest request) {
        ChatroomResponse response = chatroomService.createChatroom(request);
        log.info("[createChatroom] 생성된 채팅방 id : {}", response.getId());

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
        log.info("[findChatrooms] 검색어 : {}", name);
        FindChatroomsResponse response = chatroomService.findChatrooms(name);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "채팅방 이름 변경 (단체 톡방만 가능)")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateName(@PathVariable("id") Long chatroomId,
                                           @RequestBody UpdateChatroomRequest request) {
        chatroomService.updateName(chatroomId, request.getNewName());
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "채팅방에 멤버 추가하기")
    @PostMapping("/{id}/join")
    public ResponseEntity<BaseResponse> join(@PathVariable("id") Long chatroomId,
                                             @RequestBody JoinMemberRequest request) {
        log.info("[join] id가 {} 번인 채팅방에 {} 번 member 추가하기", chatroomId, request.getJoinMembers().toString());
        chatroomService.join(chatroomId, request);
        return ResponseEntity.ok(CHATROOM_SUCCESS);
    }

    @Operation(summary = "채팅방 나가기")
    @DeleteMapping("/{chatroomId}/memberId/{memberId}/leave")
    public ResponseEntity<BaseResponse> leave(@PathVariable("chatroomId") Long chatroomId,
                                              @PathVariable("memberId") Long memberId) {
        log.info("[leave] id가 {} 번인 채팅방에서 {} 번 member가 나감", chatroomId, memberId);
        chatroomService.leave(chatroomId, memberId);
        return ResponseEntity.ok(CHATROOM_SUCCESS);
    }

    @Operation(summary = "채팅 화면으로 들어가기")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> enter(@PathVariable("id") Long chatroomId) {
        log.info("{}번 채팅방 화면으로 이동", chatroomId);
        chatroomService.enter(chatroomId);
        return ResponseEntity.ok(CHATROOM_SUCCESS);
    }

}
