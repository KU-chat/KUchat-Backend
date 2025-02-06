package kuchat.server.domain.chat.repository;

import kuchat.server.domain.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("select c from Chat c " +
            "where (c.member1.id = :member1 and c.member2.id = :member2) " +
            "or (c.member1.id = :member2 and c.member2.id = :member1)")
    Optional<Chat> findByMembers(@Param("member1") Long id, @Param("member2") Long friendId);
}
