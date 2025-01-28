package kuchat.server.domain.block.repository;

import kuchat.server.domain.block.Block;
import kuchat.server.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {

    Optional<Block> findByBlockerAndBlocked(Member blocker, Member blocked);

    @Query("select b from Block b " +
            "join fetch b.blocked bb " +
            "where b.blocker = :member")
    Page<Block> findByBlocker(Member member, Pageable pageable);

    @Query("select b from Block b " +
            "where (b.blocked.id = :id1 and b.blocker.id = :id2) or (b.blocked.id = :id2 and b.blocker.id = :id1)")
    Optional<Block> findByIds(@Param("id1") Long member1Id, @Param("id2") Long member2Id);
}
