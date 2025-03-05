package kuchat.server.domain.chat.service;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.chat.Chat;
import kuchat.server.domain.chat.ChatMember;
import kuchat.server.domain.chat.repository.ChatMemberRepository;
import kuchat.server.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static kuchat.server.common.response.BaseResponseStatus.UNAUTHORIZED_CHAT_MEMBER;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatMemberService {
    private final ChatMemberRepository chatMemberRepository;

    @Transactional
    public void saveChatMembers(List<Member> members, Chat chat) {
        List<ChatMember> chatMembers = new ArrayList<>();
        members.forEach(member -> {
            ChatMember chatMember = new ChatMember(chat, member);
            chatMembers.add(chatMember);
        });
        chatMemberRepository.saveAllAndFlush(chatMembers);
    }

    public List<ChatMember> getChatMembersByChat(Chat chat) {
        List<ChatMember> chatMembers = chatMemberRepository.findByChat(chat);
        if (chatMembers.isEmpty()) {
            throw new KuchatException(UNAUTHORIZED_CHAT_MEMBER);
        }
        return chatMembers;
    }
}
