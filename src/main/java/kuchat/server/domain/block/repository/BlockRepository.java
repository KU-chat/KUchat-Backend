package kuchat.server.domain.block.repository;

import kuchat.server.domain.block.Block;
import kuchat.server.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {

    @Query("select b from Block b " +
            "where b.blocker = :blocker and b.blocked = :blocked")
    Optional<Block> findByMembers(@Param("blocker") Member blocker, @Param("blocked") Member blocked);

    List<Block> findByBlocker(Member member);
}
