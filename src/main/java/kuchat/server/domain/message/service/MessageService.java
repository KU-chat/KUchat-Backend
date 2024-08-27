package kuchat.server.domain.message.service;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.redis.RedisService;
import kuchat.server.domain.chatroom.Chatroom;
import kuchat.server.domain.chatroom.dto.EnterChatroomResponse;
import kuchat.server.domain.enums.MessageType;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.repository.MemberRepository;
import kuchat.server.domain.message.Message;
import kuchat.server.domain.message.dto.ChatMessage;
import kuchat.server.domain.message.dto.MessageResponse;
import kuchat.server.domain.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static kuchat.server.common.response.BaseResponseStatus.NOT_FOUND_MESSAGE;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MessageService {

    public static final Long SERVER_ID = (long) -1;
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final RedisService redisService;

    public EnterChatroomResponse enter(Chatroom chatroom) {
        log.info("[findRecentMessages] 클라이언트가 접속한 채팅방 id = {}", chatroom.getId());
        Pageable pageable = PageRequest.of(0, 20);
        List<Message> messages = messageRepository.findRecent20MessagesByChatroomId(chatroom, pageable);
        EnterChatroomResponse enterChatroomResponse = new EnterChatroomResponse(chatroom.getName());
        if (messages.isEmpty()) {
            log.info("{} 번 채팅방에 메세지가 존재하지 않음.", chatroom.getId());
            return enterChatroomResponse;
        }
        log.info("[findRecentMessages] 20개 이하 메세지 조회");
//        enterChatroomResponse.setRecentMessages(messages);
        return enterChatroomResponse;
    }

    // 멤버 1명 이상이 들어올 때, 서버가 채팅방 속 모든 클라이언트들에게 환영 메시지를 톡방에 보내는 메서드
    @Transactional
    public MessageResponse createJoinMessage(List<Member> joinMembers, Chatroom chatroom) {
        String text = joinMembers.stream()
                .map(Member::getName)
                .collect(Collectors.joining(", ")) + " 님이 입장했습니다.";

        ChatMessage joinMessage = new ChatMessage(chatroom.getId(), "TALK", SERVER_ID, text);
        log.info("[sendJoinMessage] 서버에서 새로 만든 enterMessage = {}", joinMessage.toString());
        Message saved = messageRepository.save(new Message(joinMessage, chatroom));
        return MessageResponse.serverNotice(saved);
    }

    // 멤버 1명이 나갈 때, 나갔음을 알리는 메시지를 보내는 메서드
    @Transactional
    public MessageResponse createLeaveMessage(Member member, Chatroom chatroom) {
        String text = member.getName() + " 님이 채팅방을 나갔습니다.";
        ChatMessage leaveMessage = new ChatMessage(chatroom.getId(), "LEAVE", member.getId(), text);
        Message saved = messageRepository.save(new Message(leaveMessage, chatroom));
        return MessageResponse.serverNotice(saved);
    }


    // 서버가 클라이언트로부터 받은 메세지를 처리하는 부분 (DB에 저장 및 전달)
    @Transactional
    public MessageResponse handleReceivedMessage(Chatroom chatroom, ChatMessage chatMessage) {
        Message message;
        if (chatMessage.getParentId() != null) {
            Message parent = messageRepository.findById(Long.parseLong(chatMessage.getParentId()))
                    .orElseThrow(() -> new KuchatException(NOT_FOUND_MESSAGE));
            message = new Message(chatMessage, chatroom, parent);
        }
        message = new Message(chatMessage, chatroom);
        Message saved = messageRepository.save(message);
//        String senderName = redisService.getMemberName(saved.getSenderId());
//        String senderImage = redisService.getProfileImage(saved.getSenderId());
        return new MessageResponse();
    }

}
