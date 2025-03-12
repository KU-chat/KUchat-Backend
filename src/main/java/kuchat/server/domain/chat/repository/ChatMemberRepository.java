package kuchat.server.domain.chat.repository;

import kuchat.server.domain.chat.Chat;
import kuchat.server.domain.chat.ChatMember;
import kuchat.server.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
    Optional<ChatMember> findByMemberAndChat(Member member, Chat chat);

    @Query("select cm from ChatMember cm join fetch cm.member where cm.chat = :chat")
    List<ChatMember> findByChatWithMember(@Param("chat") Chat chat);


}
