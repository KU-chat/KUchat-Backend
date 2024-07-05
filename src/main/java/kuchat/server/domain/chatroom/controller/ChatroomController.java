package kuchat.server.domain.chatroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.domain.chatroom.dto.ChatroomResponse;
import kuchat.server.domain.chatroom.dto.CreateChatroomRequest;
import kuchat.server.domain.chatroom.dto.FindChatroomsResponse;
import kuchat.server.domain.chatroom.service.ChatroomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "채팅방 이름 혹은 구성원 이름으로 채팅방 목록 조회")
    @GetMapping("")
    public ResponseEntity<FindChatroomsResponse> search(@RequestParam String name) {
        log.info("[findChatrooms] 검색어 : {}", name);
        FindChatroomsResponse response = chatroomService.findChatrooms(name);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "채팅방 이름 변경 (단체 톡방만 가능)")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateName(@PathVariable Long id,
                                           @RequestBody UpdateChatroomRequest request) {
        chatroomService.updateName(id, request.getNewName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "채팅방 제거")
    @DeleteMapping("/{id}")
    public ResponseEntity<ChatroomResponse> delete(@PathVariable Long id) {
        ChatroomResponse response = chatroomService.delete(id);
        return ResponseEntity.ok(response);
    }

}
