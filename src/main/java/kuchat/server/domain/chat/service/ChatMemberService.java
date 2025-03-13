package kuchat.server.domain.chat.service;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.chat.Chat;
import kuchat.server.domain.chat.ChatMember;
import kuchat.server.domain.chat.repository.ChatMemberRepository;
import kuchat.server.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static kuchat.server.common.response.BaseResponseStatus.*;

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
        List<ChatMember> chatMembers = chatMemberRepository.findByChatWithMember(chat);
        if (chatMembers.isEmpty()) {
            throw new KuchatException(EMPTY_CHAT_MEMBER);
        }
        return chatMembers;
    }

    public void remove(ChatMember chatMember) {
        try{
            chatMemberRepository.delete(chatMember);
        } catch (IllegalArgumentException e){
            log.info("[remove] 이미 삭제된 ChatMember 입니다. chatMember id = {}", chatMember.getChat());
            throw new KuchatException(ALREADY_LEFT_CHAT);
        }
    }

    public List<Chat> getByMemberAndChatName(Member member, String chatName, Pageable pageable) {
        return chatMemberRepository.findByMemberAndChatName(member, "%"+chatName+"%", pageable).stream()
                .map(ChatMember::getChat)
                .toList();
    }
}
