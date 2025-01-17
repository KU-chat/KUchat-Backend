package kuchat.server.domain.block.repository;

import kuchat.server.domain.block.Block;
import kuchat.server.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {

    Optional<Block> findByBlockerAndBlocked(Member blocker, Member blocked);

    List<Block> findByBlocker(Member member);
}
