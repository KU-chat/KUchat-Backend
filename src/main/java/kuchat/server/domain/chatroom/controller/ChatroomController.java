package kuchat.server.domain.chatroom.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Room", description = "채팅방")
@RequiredArgsConstructor
@RequestMapping("/chatroom")
@RestController
public class ChatroomController {
}
