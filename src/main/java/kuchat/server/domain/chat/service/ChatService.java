package kuchat.server.domain.chat.service;

import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.chat.Chat;
import kuchat.server.domain.chat.dto.CreateChatResponse;
import kuchat.server.domain.chat.repository.ChatRepository;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static kuchat.server.common.response.BaseResponseStatus.*;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final MemberService memberService;

    @Transactional
    public ResponseEntity<CreateChatResponse> create(Member member, Long friendId) {
        Optional<Chat> chats = chatRepository.findByMembers(member.getId(), friendId);
        CreateChatResponse response = findOrCreate(member, friendId, chats);
        return ResponseEntity.ok(response);
    }

    private CreateChatResponse findOrCreate(Member member, Long friendId, Optional<Chat> chats) {
        return chats.map(
                chat -> {
                    return new CreateChatResponse(ALREADY_CHAT, chat.getId());
                }
        ).orElseGet(() -> {
            Member friend = memberService.getMemberById(friendId);
            Chat chat = new Chat(member, friend);
            Chat foundChat = chatRepository.save(chat);
            return new CreateChatResponse(SUCCESS, foundChat.getId());
        });
    }


//    public FindChatroomsResponse findChatrooms(String name) {
//        List<Chatroom> chatrooms = Optional.ofNullable(chatRepository.findByNameLike(name))
//                .orElse(Collections.emptyList());
//        List<FindChatroomResponse> findChatroomResponse = chatrooms.stream()
//                .map(this::toResponse)
//                .collect(Collectors.toList());
//        return new FindChatroomsResponse(findChatroomResponse, SUCCESS);
//    }
//
//    private FindChatroomResponse toResponse(Chatroom chatroom) {
//        return new FindChatroomResponse(chatroom.getId(), chatroom.getName());
//    }
//
//    @Transactional
//    public void updateName(Long chatroomId, String newName) {
//        List<Chatroom> chatrooms = chatRepository.findByNameLike(newName);
//        if (!chatrooms.isEmpty()) {
//            throw new KuchatException(DUPLICATE_CHATROOM_NAME);
//        }
//        Chatroom chatroom = getChatroom(chatroomId);
//        chatroom.setName(newName);
//        log.info("[updateName] id = {}, 바뀐 채팅방 이름 = {}", chatroom.getId(), chatroom.getName());
//    }
//
//    public Chatroom getChatroom(Long chatroomId) {
//        Chatroom chatroom = chatRepository.findById(chatroomId)
//                .orElseThrow(() -> new KuchatException(NOT_FOUND_CHATROOM));
//        return chatroom;
//    }
//
//    private void join(Chatroom chatroom, List<Long> memberIds) {
//        List<ChatroomMember> chatroomMembers = new ArrayList<>();
//        List<Member> joinMembers = memberRepository.findAllById(memberIds);     // for문 대신 한번에 찾는 방법으로 db 접근 횟수 줄이기
//        validateMemberNum(memberIds, joinMembers);
//        for (Member member : joinMembers) {
//            ChatroomMember chatroomMember = new ChatroomMember(member, chatroom);
//            chatroomMembers.add(chatroomMember);
//        }
//        saveAll(chatroomMembers);
//
//        String result = chatroomMemberRepository.findByChatroomId(chatroom.getId()).stream()
//                .map(chatroomMember -> chatroomMember.getMember().getId().toString())
//                .collect(Collectors.joining(", "));
//        log.info("[join] 멤버 추가 후 채팅방 멤버 목록 = [{}]", result);
//    }
//
//    private void saveAll(List<ChatroomMember> chatroomMembers) {
//        try {
//            chatroomMemberRepository.saveAll(chatroomMembers);      // for문 안에서 한개씩 저장하는 대신 arraylist를 한번에 저장
//        } catch (DataAccessException e) {
//            throw new KuchatException(DB_SAVE_FAIL);
//        }
//    }
//
//    private void validateMemberNum(List<Long> memberIds, List<Member> joinMembers) {
//        if (memberIds.size() != joinMembers.size()) {
//            throw new KuchatException(NOT_FOUND_MEMBER);
//        }
//    }
//
//    @Transactional
//    public MessageResponse leave(Long chatroomId, Long memberId) {
//        Chatroom chatroom = getChatroom(chatroomId);
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
//        MessageResponse leaveMessage = messageService.createLeaveMessage(member, chatroom);
//        ChatroomMember chatroomMember = chatroomMemberRepository.findByChatroomAndMember(chatroom, member);
//        chatroomMemberRepository.delete(chatroomMember);
//
//        // 만약 채팅방에 더 이상 남아있는 사람이 없으면 해당 채팅방은 삭제된다.
//        if (chatroomMemberRepository.findByChatroomId(chatroom.getId()).isEmpty()) {
//            chatRepository.delete(chatroom);
//        }
//
//        chatroomMemberRepository.delete(chatroomMember);
//        return leaveMessage;
//    }
//
//    public List<Member> findMembersByChatroomId(Long chatroomId) {
//        List<ChatroomMember> chatrooms = chatroomMemberRepository.findByChatroomId(chatroomId);
//        return chatrooms.stream()
//                .map(ChatroomMember::getMember)
//                .toList();
//    }
}
