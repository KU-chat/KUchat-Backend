package kuchat.server.domain.chatroom.service;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.chatroom.Chatroom;
import kuchat.server.domain.chatroom.ChatroomMember;
import kuchat.server.domain.chatroom.dto.*;
import kuchat.server.domain.chatroom.repository.ChatroomMemberRepository;
import kuchat.server.domain.chatroom.repository.ChatroomRepository;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.repository.MemberRepository;
import kuchat.server.domain.message.dto.RecentMessagesResponse;
import kuchat.server.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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


    @Transactional
    public ChatroomResponse createChatroom(CreateChatroomRequest request) {
        Chatroom chatroom = chatroomRepository.save(
                Chatroom.builder()
                        .name(request.getName())
                        .build());
        log.info("[createChatroom] 생성된 채팅방 id = {}, name = {}", chatroom.getId(), chatroom.getName());
        if(request.getMemberIds().size() != 0){
            join(chatroom.getId(), request.getMemberIds());
        }
        return new ChatroomResponse(chatroom.getId());
    }

    public FindChatroomsResponse findChatrooms(String name) {
        List<Chatroom> chatrooms = chatroomRepository.findByName(name);
        List<FindChatroomResponse> findChatroomRespons = chatrooms.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return new FindChatroomsResponse(findChatroomRespons);
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

        ArrayList<ChatroomMember> chatroomMembers = new ArrayList<>();
        log.info("[join] 지금 추가한 멤버 목록 = [{}]", chatroomMembers.stream()
                .map(chatroomMember -> chatroomMember.getMember().getId().toString())
                .collect(Collectors.joining(", "))
        );

        for (Member member : joinMembers) {
            ChatroomMember chatroomMember = new ChatroomMember(member, chatroom);
            chatroomMembers.add(chatroomMember);
            member.addChatroomMember(chatroomMember);
        }

        try {
            chatroomMemberRepository.saveAll(chatroomMembers);      // for문 안에서 한개씩 저장하는 대신 arraylist를 한번에 저장
        } catch (DataAccessException e) {
            throw new KuchatException(DB_SAVE_FAIL);
        }

        log.info("[join] 멤버 추가 후 채팅방 멤버 목록 = [{}}]", chatroom.getChatroomMembers().stream()
                .map(chatroomMember -> chatroomMember.getMember().getId().toString())
                .collect(Collectors.joining(", "))
        );

        messageService.sendEnterMessage(joinMembers, chatroom);
    }

    @Transactional
    public void leave(Long chatroomId, Long memberId) {
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_CHATROOM));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
        messageService.sendLeaveMessage(member, chatroom);

        ChatroomMember chatroomMember = chatroomMemberRepository.findByChatroomAndMember(chatroom, member);
        int memberNum = chatroom.deleteChatroomMember(chatroomMember);
        member.deleteChatroomMember(chatroomMember);

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
