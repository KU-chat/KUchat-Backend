package kuchat.server.domain.message.service;

import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.chatroom.Chatroom;
import kuchat.server.domain.chatroom.repository.ChatroomRepository;
import kuchat.server.domain.enums.MessageType;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.repository.MemberRepository;
import kuchat.server.domain.message.ChatMessageEvent;
import kuchat.server.domain.message.Message;
import kuchat.server.domain.message.dto.ChatMessage;
import kuchat.server.domain.message.dto.RecentMessagesResponse;
import kuchat.server.domain.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static kuchat.server.common.exception.BaseResponse.NOT_FOUND_MESSAGE;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatroomRepository chatroomRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;


    public RecentMessagesResponse findRecentMessages(Chatroom chatroom) {
        Pageable pageable = PageRequest.of(0, 20);
        List<Message> messages = messageRepository.findRecent20MessagesByChatroomId(chatroom, pageable);
        return new RecentMessagesResponse(messages);
    }

    // Message 객체 생성 후 DB에 저장
    @Transactional
    public Message createMessage() {
        return null;
    }

    // 멤버 1명 이상이 들어올 때, 서버가 채팅방 속 모든 클라이언트들에게 환영 메시지를 톡방에 보내는 메서드
    public void sendEnterMessage(List<Member> joinMembers, Chatroom chatroom) {
        ChatMessage enterMessage = createEnterMessage(joinMembers, chatroom);
        ChatMessageEvent chatMessageEvent = new ChatMessageEvent(this, enterMessage);
        eventPublisher.publishEvent(chatMessageEvent);      // WebSocketHandler 의 onChatMessageEvent 메서드가 실행됨
    }

    private ChatMessage createEnterMessage(List<Member> joinMembers, Chatroom chatroom) {
        String text = "👋🏻 " + joinMembers.stream()
                .map(member -> member.getName())
                .collect(Collectors.joining(", ")) + " 님이 입장했습니다.";

        return ChatMessage.builder()
                .messageId(null)
                .chatroomId(chatroom.getId())
                .messageType(MessageType.JOIN)
                .senderId(null)
                .text(text)
                .build();
    }

    // 멤버 1명이 나갈 때, 나갔음을 알리는 메시지를 보내는 메서드
    public void sendLeaveMessage(Member member, Chatroom chatroom) {
        ChatMessage leaveMessage = createLeaveMessage(member, chatroom);
        ChatMessageEvent chatMessageEvent = new ChatMessageEvent(this, leaveMessage);
        eventPublisher.publishEvent(chatMessageEvent);      // WebSocketHandler 의 onChatMessageEvent 메서드가 실행됨

    }

    private ChatMessage createLeaveMessage(Member member, Chatroom chatroom) {
        String text = "👋🏻 " + member.getName() + " 님이 채팅방을 나갔습니다.";

        return ChatMessage.builder()
                .messageId(null)
                .chatroomId(chatroom.getId())
                .messageType(MessageType.LEAVE)
                .senderId(null)
                .text(text)
                .build();
    }

    // 서버가 클라이언트로부터 받은 메세지를 처리하는 부분 (DB에 저장 및 전달)
    @Transactional
    public void handleReceivedMessage(ChatMessage chatMessage) {
        Chatroom chatroom = chatroomRepository.findById(chatMessage.getChatroomId())
                .orElseThrow(() -> new KuchatException(BaseResponse.NOT_FOUND_CHATROOM));

        Message message;
        if (chatMessage.getParentId() != null) {
            Message parent = messageRepository.findById(chatMessage.getParentId())
                    .orElseThrow(() -> new KuchatException(NOT_FOUND_MESSAGE));
            message = new Message(chatMessage, chatroom, parent);
        }
        message = new Message(chatMessage, chatroom);

        messageRepository.save(message);
        ChatMessageEvent chatMessageEvent = new ChatMessageEvent(this, chatMessage);
        eventPublisher.publishEvent(chatMessageEvent);      // WebSocketHandler 의 onChatMessageEvent 메서드가 실행됨
    }

}
