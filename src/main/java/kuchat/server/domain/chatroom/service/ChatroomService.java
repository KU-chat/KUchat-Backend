package kuchat.server.domain.chatroom.service;

import kuchat.server.common.exception.KuchatException;
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
import kuchat.server.domain.message.dto.MessageResponse;
import kuchat.server.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static kuchat.server.common.response.BaseResponseStatus.*;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatroomService {

    private final ChatroomRepository chatroomRepository;
    private final ChatroomMemberRepository chatroomMemberRepository;
    private final MemberRepository memberRepository;
    private final MessageService messageService;


    @Transactional
    public ChatroomResponse create(CreateChatroomRequest request) {
        List<Chatroom> chatrooms = chatroomRepository.findByName(request.getName());
        if (!chatrooms.isEmpty()) {
            throw new KuchatException(DUPLICATE_CHATROOM_NAME);
        }
        Chatroom chatroom = chatroomRepository.save(
                Chatroom.builder()
                        .name(request.getName())
                        .build());
        log.info("[createChatroom] 생성된 채팅방 id = {}, name = {}", chatroom.getId(), chatroom.getName());
        join(chatroom, request.getMemberIds());
        return new ChatroomResponse(chatroom.getId(), SUCCESS);
    }


    public FindChatroomsResponse findChatrooms(String name) {
        List<Chatroom> chatrooms = Optional.ofNullable(chatroomRepository.findByName(name))
                .orElse(Collections.emptyList());
        List<FindChatroomResponse> findChatroomResponse = chatrooms.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return new FindChatroomsResponse(findChatroomResponse, SUCCESS);
    }

    private FindChatroomResponse toResponse(Chatroom chatroom) {
        return new FindChatroomResponse(chatroom.getId(), chatroom.getName());
    }

    @Transactional
    public void updateName(Long chatroomId, String newName) {
        List<Chatroom> chatrooms = chatroomRepository.findByName(newName);
        if (!chatrooms.isEmpty()) {
            throw new KuchatException(DUPLICATE_CHATROOM_NAME);
        }
        Chatroom chatroom = getChatroom(chatroomId);
        chatroom.setName(newName);
        log.info("[updateName] id = {}, 바뀐 채팅방 이름 = {}", chatroom.getId(), chatroom.getName());
    }

    public Chatroom getChatroom(Long chatroomId) {
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_CHATROOM));
        return chatroom;
    }

    private void join(Chatroom chatroom, List<Long> memberIds) {
        List<ChatroomMember> chatroomMembers = new ArrayList<>();
        List<Member> joinMembers = memberRepository.findAllById(memberIds);     // for문 대신 한번에 찾는 방법으로 db 접근 횟수 줄이기
        validateMemberNum(memberIds, joinMembers);
        for (Member member : joinMembers) {
            ChatroomMember chatroomMember = new ChatroomMember(member, chatroom);
            chatroomMembers.add(chatroomMember);
        }
        saveAll(chatroomMembers);

        String result = chatroomMemberRepository.findByChatroomId(chatroom.getId()).stream()
                .map(chatroomMember -> chatroomMember.getMember().getId().toString())
                .collect(Collectors.joining(", "));
        log.info("[join] 멤버 추가 후 채팅방 멤버 목록 = [{}]", result);
    }

    private void saveAll(List<ChatroomMember> chatroomMembers) {
        try {
            chatroomMemberRepository.saveAll(chatroomMembers);      // for문 안에서 한개씩 저장하는 대신 arraylist를 한번에 저장
        } catch (DataAccessException e) {
            throw new KuchatException(DB_SAVE_FAIL);
        }
    }

    private void validateMemberNum(List<Long> memberIds, List<Member> joinMembers) {
        if (memberIds.size() != joinMembers.size()) {
            throw new KuchatException(NOT_FOUND_MEMBER);
        }
    }

    @Transactional
    public MessageResponse leave(Long chatroomId, Long memberId) {
        Chatroom chatroom = getChatroom(chatroomId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
        MessageResponse leaveMessage = messageService.createLeaveMessage(member, chatroom);
        ChatroomMember chatroomMember = chatroomMemberRepository.findByChatroomAndMember(chatroom, member);
        chatroomMemberRepository.delete(chatroomMember);

        // 만약 채팅방에 더 이상 남아있는 사람이 없으면 해당 채팅방은 삭제된다.
        if (chatroomMemberRepository.findByChatroomId(chatroom.getId()).isEmpty()) {
            chatroomRepository.delete(chatroom);
        }

        chatroomMemberRepository.delete(chatroomMember);
        return leaveMessage;
    }

    public List<Member> findMembersByChatroomId(Long chatroomId) {
        List<ChatroomMember> chatrooms = chatroomMemberRepository.findByChatroomId(chatroomId);
        return chatrooms.stream()
                .map(ChatroomMember::getMember)
                .toList();
    }
}
