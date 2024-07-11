package kuchat.server.domain.message.service;

import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.redis.RedisPublisher;
import kuchat.server.domain.chatroom.Chatroom;
import kuchat.server.domain.chatroom.repository.ChatroomRepository;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.message.ChatMessageEvent;
import kuchat.server.domain.message.Message;
import kuchat.server.domain.message.dto.ChatMessage;
import kuchat.server.domain.message.dto.ChatroomJoinRequest;
import kuchat.server.domain.message.dto.RecentMessagesResponse;
import kuchat.server.domain.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static kuchat.server.common.exception.BaseResponse.DB_SAVE_FAIL;
import static kuchat.server.common.exception.BaseResponse.NOT_FOUND_MESSAGE;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MessageService {

    public static final Long SERVER_ID = (long) -1;
    private final MessageRepository messageRepository;
    private final ChatroomRepository chatroomRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisPublisher redisPublisher;


    public RecentMessagesResponse findRecentMessages(Chatroom chatroom) {
        log.info("[findRecentMessages] 클라이언트가 접속한 채팅방 id = {}", chatroom.getId());
        Pageable pageable = PageRequest.of(0, 20);
        List<Message> messages = messageRepository.findRecent20MessagesByChatroomId(chatroom, pageable);
        log.info("[findRecentMessages] 조회한 20개 이하 메세지 = {}", messages.toString());
        return new RecentMessagesResponse(messages);
    }

    // 멤버 1명 이상이 들어올 때, 서버가 채팅방 속 모든 클라이언트들에게 환영 메시지를 톡방에 보내는 메서드
    @Transactional
    public void sendJoinMessage(List<Member> joinMembers, Chatroom chatroom, ChannelTopic topic) {
        ChatroomJoinRequest joinMessage = createEnterMessage(joinMembers, chatroom);
        log.info("[sendJoinMessage] 서버에서 새로 만든 enterMessage = {}", joinMessage.toString());
        messageRepository.save(new Message(joinMessage, chatroom));
        redisPublisher.publish(topic, new ChatMessage(joinMessage));
    }

    private ChatroomJoinRequest createEnterMessage(List<Member> joinMembers, Chatroom chatroom) {
        String text = joinMembers.stream()
                .map(Member::getName)
                .collect(Collectors.joining(", ")) + " 님이 입장했습니다.";

        List<Long> memberIds = joinMembers.stream()
                .map(Member::getId).toList();

        return new ChatroomJoinRequest(chatroom.getId(), memberIds, text);
    }

    // 멤버 1명이 나갈 때, 나갔음을 알리는 메시지를 보내는 메서드
    @Transactional
    public void sendLeaveMessage(Member member, Chatroom chatroom, ChannelTopic topic) {
        ChatMessage leaveMessage = createLeaveMessage(member, chatroom);
        messageRepository.save(new Message(leaveMessage, chatroom));
        redisPublisher.publish(topic, leaveMessage);
    }

    private ChatMessage createLeaveMessage(Member member, Chatroom chatroom) {
        String text = member.getName() + " 님이 채팅방을 나갔습니다.";

        return ChatMessage.builder()
                .chatroomId(chatroom.getId())
                .senderId(member.getId())
                .text(text)
                .build();
    }

    // 서버가 클라이언트로부터 받은 메세지를 처리하는 부분 (DB에 저장 및 전달)
    @Transactional
    public void handleReceivedMessage(ChannelTopic topic, ChatMessage chatMessage) {
        Chatroom chatroom = chatroomRepository.findById(chatMessage.getChatroomId())
                .orElseThrow(() -> new KuchatException(BaseResponse.NOT_FOUND_CHATROOM));

        Message message;
        if (chatMessage.getParentId() != null) {
            Message parent = messageRepository.findById(chatMessage.getParentId())
                    .orElseThrow(() -> new KuchatException(NOT_FOUND_MESSAGE));
            message = new Message(chatMessage, chatroom, parent);
        }
        message = new Message(chatMessage, chatroom);

        try {
            // 이렇게 하면 generatedMessageId 는 모든 채팅방에 있는 메세지들에 대해서 1씩 증가하도록 설정된다.
            // 한 채팅방에 대해서만 generatedMessageId 가 1씩 증가하도록 만들고 싶은데, 이걸 어떻게 구현해야할까..
            messageRepository.save(message);
        } catch (DataAccessException e) {
            throw new KuchatException(DB_SAVE_FAIL);
        }
        redisPublisher.publish(topic, chatMessage);
    }

}
