package kuchat.server.domain.block.repository;

import kuchat.server.domain.block.Block;
import kuchat.server.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {

    Optional<Block> findByBlockerAndBlocked(Member blocker, Member blocked);

    List<Block> findByBlocker(Member member);

    @Query("select b from Block b " +
            "where (b.blocked.id = :id1 and b.blocker.id = :id2) or (b.blocked.id = :id2 and b.blocker.id = :id1)")
    Optional<Block> findByIds(@Param("id1") Long member1Id, @Param("id2") Long member2Id);
}
