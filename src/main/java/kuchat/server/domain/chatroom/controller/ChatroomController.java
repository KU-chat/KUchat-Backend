package kuchat.server.domain.chatroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.chatroom.dto.ChatroomResponse;
import kuchat.server.domain.chatroom.dto.CreateChatroomRequest;
import kuchat.server.domain.chatroom.dto.FindChatroomsResponse;
import kuchat.server.domain.chatroom.dto.UpdateChatroomRequest;
import kuchat.server.domain.chatroom.service.ChatroomService;
import kuchat.server.domain.message.dto.RecentMessagesResponse;
import kuchat.server.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static kuchat.server.common.exception.BaseResponse.EMPTY_CHATROOM;

@Slf4j
@Tag(name = "Chatroom", description = "채팅방")
@RequiredArgsConstructor
@RequestMapping("/chatroom")
@RestController
public class ChatroomController {

    private final ChatroomService chatroomService;
    private final MessageService messageService;

    @Operation(summary = "채팅방 생성")
    @PostMapping("")
    public ResponseEntity<ChatroomResponse> create(@RequestBody CreateChatroomRequest request) {

        if (request.getMemberIds().isEmpty()) {
            throw new KuchatException(EMPTY_CHATROOM);
        }

        log.info("[create] 채팅방 생성 요청 request = {}", request.toString());
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
    public ResponseEntity<Void> updateName(@PathVariable("id") Long chatroomId,
                                           @RequestBody UpdateChatroomRequest request) {
        String newName = request.getNewName();
        log.info("[updateName] 채팅방 번호 = {}, 바꿀 이름 = {}", chatroomId, newName);
        if (newName == null) {
            log.info("[updateName] 이름 안바꿉니다~~");
            return ResponseEntity.ok().build();
        }
        chatroomService.updateName(chatroomId, request.getNewName());
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "채팅 화면으로 들어가기 (최근 20개 톡 가져오기)")
    @GetMapping("/{id}/enter")
    public ResponseEntity<RecentMessagesResponse> enter(@PathVariable("id") Long chatroomId) {
        log.info("{}번 채팅방 화면으로 이동", chatroomId);
        RecentMessagesResponse response = messageService.enter(chatroomId);        // 최근 20개 톡 가져오기
        return ResponseEntity.ok().body(response);
    }

}
