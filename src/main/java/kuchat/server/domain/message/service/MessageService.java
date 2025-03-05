package kuchat.server.domain.message.service;

import kuchat.server.domain.chat.Chat;
import kuchat.server.domain.chat.dto.CreateChatResponse;
import kuchat.server.domain.message.Message;
import kuchat.server.domain.message.dto.RecentMessageResponses;
import kuchat.server.domain.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kuchat.server.common.response.BaseResponseStatus.SUCCESS;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageRepository messageRepository;

    public void notifyNewChat(Chat chat, List<Long> memberIds) {
        if (chat.isGroup()) {
            // 단체 채팅방이면 `/topic/new-chat`으로 전송 (모든 사용자에게 전송)
            // 직접 simpMessagingTemplate 사용하지 말고, MessageService의 메서드 구현해서 활용하기
            simpMessagingTemplate.convertAndSend("/topic/new-chat",
                    new CreateChatResponse(SUCCESS, chat.getId()));
        } else {
            // 개인 채팅방이면 `/queue/new-chat`으로 전송 (각 사용자에게 개별 전송)
            for (Long memberId : memberIds) {
                simpMessagingTemplate.convertAndSendToUser(memberId.toString(),
                        "/queue/new-chat",
                        new CreateChatResponse(SUCCESS, chat.getId()));
            }
        }
    }

    public RecentMessageResponses getRecentMessages(Chat chat, Pageable pageable) {
        Page<Message> recentMessages = messageRepository.findRecent30MessagesByChat(chat, pageable);
        return null;
    }
}
