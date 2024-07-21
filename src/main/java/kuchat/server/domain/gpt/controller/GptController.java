package kuchat.server.domain.gpt.controller;

import io.swagger.v3.oas.annotations.Operation;
import kuchat.server.domain.gpt.GptService;
import kuchat.server.domain.gpt.dto.TopicsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/gpt")
@RestController
public class GptController {

    @Value("${gpt.api.key}")
    private String apiKey;

    @Value("${gpt.api.url}")
    private String apiUrl;

    @Value("${gpt.model}")
    private String model;

    private final GptService gptService;

//    @Operation(summary = "AI 대화추천 기능")
//    @PostMapping("/chatroom/{chatroomId}")
//    public ResponseEntity<TopicsResponse> suggestTopic(@PathVariable("chatroomId") Long chatroomId){
//        log.info("[suggestTopic] chatroomId = {} 인 채팅방에 대화 추천 기능ㄱㄱ: ", chatroomId);
//        TopicsResponse response = gptService.getTopics(chatroomId);
//        return ResponseEntity.ok();
//    }
}