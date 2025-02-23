package kuchat.server.domain.chat.repository;

import kuchat.server.domain.chat.Chat;
import kuchat.server.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {


    @Query("select c from Chat c where c.id in " +
            "(select cm.chat.id " +
            "from ChatMember cm " +
            "where cm.member in :members)")
    List<Chat> findByParticipants(@Param("members") List<Member> members);

    @Query("select c from Chat c where c.id in " +
            "(select cm.chat.id " +
            "from ChatMember cm " +
            "where cm.member in :members " +
            "group by cm.chat.id having count(cm.member) = :size)")         // 각 Chat별로 참여 멤버 수를 계산해서 비교
    List<Chat> findByExactParticipants(@Param("members") List<Member> members, @Param("size") int memberNum);
}
