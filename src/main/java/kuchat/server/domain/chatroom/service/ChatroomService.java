package kuchat.server.domain.chatroom.service;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.redis.RedisSubscriber;
import kuchat.server.domain.chatroom.Chatroom;
import kuchat.server.domain.chatroom.ChatroomMember;
import kuchat.server.domain.chatroom.dto.ChatroomResponse;
import kuchat.server.domain.chatroom.dto.CreateChatroomRequest;
import kuchat.server.domain.chatroom.dto.FindChatroomResponse;
import kuchat.server.domain.chatroom.dto.FindChatroomsResponse;
import kuchat.server.domain.chatroom.repository.ChatroomMemberRepository;
import kuchat.server.domain.chatroom.repository.ChatroomRepository;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.repository.MemberRepository;
import kuchat.server.domain.message.dto.RecentMessagesResponse;
import kuchat.server.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static kuchat.server.common.exception.BaseResponse.*;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatroomService {

    private final ChatroomRepository chatroomRepository;
    private final ChatroomMemberRepository chatroomMemberRepository;
    private final MemberRepository memberRepository;
    private final MessageService messageService;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisSubscriber redisSubscriber;

    private Map<Long, ChannelTopic> topics = new HashMap<>();


    @Transactional
    public ChatroomResponse create(CreateChatroomRequest request) {
        Chatroom chatroom = chatroomRepository.save(
                Chatroom.builder()
                        .name(request.getName())
                        .build());
        log.info("[createChatroom] 생성된 채팅방 id = {}, name = {}", chatroom.getId(), chatroom.getName());
        join(chatroom.getId(), request.getMemberIds());
        return new ChatroomResponse(chatroom.getId());
    }

    public ChannelTopic getTopic(Long chatroomId) {
        ChannelTopic topic = topics.get(chatroomId);
        if (topic == null) {
            topic = new ChannelTopic(chatroomId.toString());
            redisMessageListenerContainer.addMessageListener(redisSubscriber, topic);
            topics.put(chatroomId, topic);
        }

        return topic;
    }

    public FindChatroomsResponse findChatrooms(String name) {
        List<Chatroom> chatrooms = chatroomRepository.findByName(name);
        List<FindChatroomResponse> findChatroomResponse = chatrooms.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return new FindChatroomsResponse(findChatroomResponse);
    }


    private FindChatroomResponse toResponse(Chatroom chatroom) {
        return new FindChatroomResponse(chatroom.getId(), chatroom.getName());
    }

    @Transactional
    public void updateName(Long chatroomId, String newName) {
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_CHATROOM));
        chatroom.setName(newName);
        log.info("[updateName] id = {}, 바뀐 채팅방 이름 = {}", chatroom.getId(), chatroom.getName());
    }

    @Transactional
    public void join(Long chatroomId, List<Long> memberIds) {
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_CHATROOM));
        List<Member> joinMembers = memberRepository.findAllById(memberIds);     // for문 대신 한번에 찾는 방법으로 db 접근 횟수 줄이기
        if (memberIds.size() != joinMembers.size()) {
            throw new KuchatException(NOT_FOUND_MEMBER);
        }

        List<ChatroomMember> chatroomMembers = new ArrayList<>();

        for (Member member : joinMembers) {
            ChatroomMember chatroomMember = new ChatroomMember(member, chatroom);
            chatroomMembers.add(chatroomMember);
            member.addChatroom(chatroomMember);
            chatroom.addMember(chatroomMember);
        }

        try {
            chatroomMemberRepository.saveAll(chatroomMembers);      // for문 안에서 한개씩 저장하는 대신 arraylist를 한번에 저장
        } catch (DataAccessException e) {
            throw new KuchatException(DB_SAVE_FAIL);
        }

        log.info("[join] 멤버 추가 후 채팅방 멤버 목록 = [{}]", chatroom.getChatroomMembers().stream()
                .map(chatroomMember -> chatroomMember.getMember().getId().toString())
                .collect(Collectors.joining(", "))
        );

        ChannelTopic topic = getTopic(chatroom.getId());
        messageService.sendJoinMessage(joinMembers, chatroom, topic);
    }

    @Transactional
    public void leave(Long chatroomId, Long memberId) {
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_CHATROOM));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
        ChannelTopic topic = getTopic(chatroom.getId());
        messageService.sendLeaveMessage(member, chatroom, topic);

        ChatroomMember chatroomMember = chatroomMemberRepository.findByChatroomAndMember(chatroom, member);
        int memberNum = chatroom.deleteMember(chatroomMember);
        member.deleteChatroom(chatroomMember);

        // 만약 채팅방에 더 이상 남아있는 사람이 없으면 해당 채팅방은 삭제된다.
        if (memberNum == 0) {
            delete(chatroom);
        }
        chatroomMemberRepository.delete(chatroomMember);
    }

    @Transactional
    protected void delete(Chatroom chatroom) {
        Set<ChatroomMember> chatroomMembers = chatroom.getChatroomMembers();
        chatroomMemberRepository.deleteAll(chatroomMembers);
        chatroomRepository.delete(chatroom);
    }

    public RecentMessagesResponse enter(Long chatroomId) {
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_CHATROOM));
        return messageService.findRecentMessages(chatroom);       // 최근 20개 톡 가져오기
    }

}
