package kuchat.server.domain.relation.block.repository;

import kuchat.server.domain.relation.block.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
}
