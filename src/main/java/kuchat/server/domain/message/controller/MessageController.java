package kuchat.server.domain.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Message", description = "메시지")
@RequiredArgsConstructor
@RequestMapping("/message")
@RestController
public class MessageController {

    private final MessageService messageService;

//    @Operation(summary = "메세지 전송")


}
