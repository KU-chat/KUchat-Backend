package kuchat.server.domain.chat.service;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.domain.chat.Chat;
import kuchat.server.domain.chat.ChatMember;
import kuchat.server.domain.chat.dto.CreateChatRequest;
import kuchat.server.domain.chat.dto.CreateChatResponse;
import kuchat.server.domain.chat.repository.ChatMemberRepository;
import kuchat.server.domain.chat.repository.ChatRepository;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.service.MemberService;
import kuchat.server.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static kuchat.server.common.response.BaseResponseStatus.*;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final MemberService memberService;
    private final MessageService messageService;

    @Value("${default.profile.address}")
    private String defaultImage;

    @Transactional
    public ResponseEntity<BaseResponse> create(Member creator, CreateChatRequest request) {
        log.info("[create] {} 멤버가 {} 를 구성원으로 하는 채팅방 생성 요청", creator.getId(), request.getFriends().toString());
        List<Member> members = getParticipants(creator, request);
        Chat chat = new Chat(request.getName(), defaultImage);
        saveChatMembers(members, chat);

        log.info("[create] chat의 chatmembers가 업데이트 됐는지 확인 = {}", chat.getChatMembers().toString());
        Chat savedChat = chatRepository.save(chat);

        // TODO. 채팅방 생성 알림 보내기
//        messageService.notifyNewChat(chat, request.getFriends());
        return ResponseEntity.ok(new CreateChatResponse(SUCCESS, savedChat.getId()));
    }

    private List<Member> getParticipants(Member creator, CreateChatRequest request) {
        List<Member> members = new ArrayList<>(request.getFriends().stream()
                .map(memberService::getMemberById)
                .toList());
        members.add(creator);
        return members;
    }

    private void saveChatMembers(List<Member> members, Chat chat) {
        members.forEach(member -> {
            ChatMember chatMember = new ChatMember(chat, member);
            chatMemberRepository.save(chatMember);
        });
    }

    public ResponseEntity<BaseResponse> validateEnter(Member member, Long chatId) {
        // TODO. 채팅방에 새로운 메시지 읽음 처리
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_CHAT));

        chatMemberRepository.findByMemberAndChat(member, chat)
                .orElseThrow(() -> new KuchatException(UNAUTHORIZED_CHAT_MEMBER));

        return ResponseEntity.ok(new BaseResponse(SUCCESS));
    }

}
