package kuchat.server.domain.chat.service;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.chat.Chat;
import kuchat.server.domain.chat.ChatMember;
import kuchat.server.domain.chat.dto.ChatMemberResponse;
import kuchat.server.domain.chat.dto.CreateChatRequest;
import kuchat.server.domain.chat.dto.CreateChatResponse;
import kuchat.server.domain.chat.dto.ViewChatResponse;
import kuchat.server.domain.chat.repository.ChatRepository;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kuchat.server.common.response.BaseResponseStatus.*;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatMemberService chatMemberService;
    private final MemberService memberService;

    @Value("${default.profile.address}")
    private String defaultImage;

    @Transactional
    public CreateChatResponse create(Member creator, CreateChatRequest request) {
        log.info("[create] {} 멤버가 {} 를 구성원으로 하는 채팅방 생성 요청", creator.getId(), request.getFriends().toString());
        List<Member> members = getParticipants(creator, request);
        Chat chat = new Chat(request.getName(), defaultImage);

        log.info("[create] chat의 chatmembers가 업데이트 됐는지 확인 = {}", chat.getChatMembers().toString());
        Chat savedChat = chatRepository.save(chat);
        chatMemberService.saveChatMembers(members, chat);

//        TODO. 채팅방 생성 알림 보내기
//        messageService.notifyNewChat(chat, request.getFriends());
        return new CreateChatResponse(SUCCESS, savedChat.getId());
    }

    public ViewChatResponse validateEnter(Member member, Long chatId) {
        Chat chat = getChatById(chatId);
        List<ChatMember> chatMembers = chatMemberService.getChatMembersByChat(chat);
        checkMemberInChat(member, chatMembers, chat);
        return new ViewChatResponse(SUCCESS, chat);
    }


    private void checkMemberInChat(Member member, List<ChatMember> chatMembers, Chat chat) {
        chatMembers.stream()
                .filter(chatMember -> chatMember.matches(chat, member))
                .findFirst()
                .orElseThrow(() -> new KuchatException(UNAUTHORIZED_CHAT_MEMBER));
    }

    private List<Member> getParticipants(Member creator, CreateChatRequest request) {
        List<Member> members = memberService.getMembersByIds(request.getFriends());
        members.add(creator);
        return members;
    }

    private List<ChatMemberResponse> getChatMemberResponses(List<ChatMember> chatMembers) {
        return chatMembers.stream()
                .map(ChatMemberResponse::new)
                .toList();
    }

    public Chat getChatById(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_CHAT));
    }
}
