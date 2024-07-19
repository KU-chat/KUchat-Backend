package kuchat.server.domain.gpt;

import kuchat.server.domain.chatroom.Chatroom;
import kuchat.server.domain.chatroom.service.ChatroomService;
import kuchat.server.domain.gpt.dto.RecentMessage;
import kuchat.server.domain.gpt.dto.TopicsResponse;
import kuchat.server.domain.message.Message;
import kuchat.server.domain.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GptService {

    private final RestTemplate restTemplate;
    private final ChatroomService chatroomService;
    private final MessageRepository messageRepository;

    public TopicsResponse getTopics(Long chatroomId) {
        log.info("[getTopics] chatroomId: " + chatroomId);
        Chatroom chatroom = chatroomService.getChatroom(chatroomId);
        Pageable pageable = PageRequest.of(0, 20);
        List<Message> messages = messageRepository.findRecent20MessagesByChatroomId(chatroom, pageable);

        return null;
    }
}
