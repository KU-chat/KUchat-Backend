package kuchat.server.domain.chat.repository;

import kuchat.server.domain.chat.Chat;
import kuchat.server.domain.chat.ChatMember;
import kuchat.server.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
    Optional<ChatMember> findByMemberAndChat(Member member, Chat chat);

    List<ChatMember> findByChat(Chat chat);
}
