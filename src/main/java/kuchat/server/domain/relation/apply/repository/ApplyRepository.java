package kuchat.server.domain.relation.apply.repository;

import kuchat.server.domain.friend.Friend;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.relation.apply.Apply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplyRepository extends JpaRepository<Apply, Long> {

    @Query("select a from Apply a " +
            "where a.sender = :sender and a.receiver = :receiver")
    Optional<Apply> findBySenderAndReceiver(@Param("sender")Member sender, @Param("receiver") Member receiver);

    @Query("select a from Apply a " +
            "where a.sender = :sender")
    List<Apply> findAllBySender(@Param("sender") Member sender);

    @Query("select a from Apply a " +
            "where a.receiver = :receiver")
    List<Apply> findAllByReceiver(@Param("receiver")Member receiver);
}
