package kuchat.server.domain.chat.repository;

import kuchat.server.domain.chat.Chat;
import kuchat.server.domain.chat.ChatMember;
import kuchat.server.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

    @Query("select cm from ChatMember cm join fetch cm.member where cm.chat = :chat")
    List<ChatMember> findByChatWithMember(@Param("chat") Chat chat);

    @Query("select cm " +
            "from ChatMember cm join fetch cm.chat " +
            "where cm.member = :member and cm.chat.name like :chatName " +
            "order by cm.chat.modifiedDate DESC")
    Page<ChatMember> findByMemberAndChatName(@Param("member") Member member,
                                             @Param("chatName") String chatName,
                                             Pageable pageable);
}
