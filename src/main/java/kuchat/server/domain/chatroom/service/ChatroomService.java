package kuchat.server.domain.chatroom.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.socket.WebSocketHandler;
import kuchat.server.domain.chatroom.Chatroom;
import kuchat.server.domain.chatroom.ChatroomMember;
import kuchat.server.domain.chatroom.dto.*;
import kuchat.server.domain.chatroom.repository.ChatroomMemberRepository;
import kuchat.server.domain.chatroom.repository.ChatroomRepository;
import kuchat.server.domain.enums.MessageType;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.repository.MemberRepository;
import kuchat.server.domain.message.dto.ChatMessage;
import kuchat.server.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private final WebSocketHandler webSocketHandler;

    @Transactional
    public void handlerActions(WebSocketSession session, ChatMessage chatMessage) {
        if (chatMessage.getMessageType() == MessageType.ENTER) {
            if (webSocketHandler.addSession(session)) {
                chatMessage.setText("👋🏻 " + chatMessage.getSender() + " 님이 입장했습니다.");
            }
        }
        sendMessage(session, chatMessage);
    }

    //    @Transactional
    private <T> void sendMessage(WebSocketSession session, ChatMessage chatMessage) {
//        try{
//            session.sendMessage(chatMessage);

//        } catch (IOException e){

//        }
    }

    @Transactional
    public ChatroomResponse createChatroom(CreateChatroomRequest request) {
        Chatroom chatroom = chatroomRepository.save(
                Chatroom.builder()
                        .name(request.getName())
                        .build());
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
                .orElseThrow(() -> new KuchatException(CHATROOM_NOTFOUND));
        chatroom.updateName(newName);
    }

    @Transactional
    public void join(Long chatroomId, JoinMemberRequest request) {
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new KuchatException(CHATROOM_NOTFOUND));

        List<Long> joinMembersId = request.getJoinMembers();
        List<Member> joinMembers = memberRepository.findAllById(joinMembersId);     // for문 대신 한번에 찾는 방법으로 db 접근 횟수 줄이기
        if (joinMembersId.size() != joinMembers.size()) {
            throw new KuchatException(MEMBER_NOTFOUND);
        }

        ArrayList<ChatroomMember> chatroomMembers = new ArrayList<>();
        for (Member member : joinMembers) {
            ChatroomMember chatroomMember = new ChatroomMember(member, chatroom);
            chatroomMembers.add(chatroomMember);
        }

        try {
            chatroomMemberRepository.saveAll(chatroomMembers);      // for문 안에서 한개씩 저장하는 대신 arraylist를 한번에 저장
        } catch (DataAccessException e) {
            throw new KuchatException(DB_SAVE_FAIL);
        }
    }

    @Transactional
    public void leave(Long chatroomId, Long memberId) {
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new KuchatException(CHATROOM_NOTFOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new KuchatException(MEMBER_NOTFOUND));

        ChatroomMember chatroomMember = chatroomMemberRepository.findByChatroomAndMember(chatroom, member);
        int memberNum = chatroom.deleteChatroomMember(chatroomMember);

        // 만약 채팅방에 더 이상 남아있는 사람이 없으면 해당 채팅방은 삭제된다.
        if (memberNum == 0) {
            delete(chatroom);
        } else {
            chatroomMemberRepository.delete(chatroomMember);
        }
    }

    @Transactional
    protected void delete(Chatroom chatroom) {
        HashSet<ChatroomMember> chatroomMembers = chatroom.getChatroomMembers();
        chatroomMemberRepository.deleteAll(chatroomMembers);
        chatroomRepository.delete(chatroom);
    }

    public void enter(Long chatroomId) {
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new KuchatException(CHATROOM_NOTFOUND));
        messageService.findRecentMessages(chatroomId);
    }
}
