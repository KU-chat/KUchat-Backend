package kuchat.server.domain.friend.repository;

import kuchat.server.domain.friend.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("select f from Friend f " +
            "where f.sender.id = :id and f.friendType = 'PENDING'")
    List<Friend> findAllBySenderId(@Param("id") Long id);

    @Query("select f from Friend f " +
            "where f.receiver.id = :id and f.friendType = 'PENDING'")
    List<Friend> findAllByReceiverId(Long id);

    @Query("select f from Friend f " +
            "where f.friendType = 'PENDING' " +
            "and f.sender.id = :senderId and f.receiver.id = :id")
    Optional<Friend> findBySenderIdAndReceiverId(@Param("senderId")Long senderId, @Param("id")Long id);

    @Query("select f from Friend f " +
            "where f.friendType = 'FRIEND' and " +
            "((f.sender.id = :id and f.receiver.name like %:name%) or " +
            "(f.receiver.id = :id and f.sender.name like %:name%))")
    List<Friend> findAllByName(@Param("id") Long id, @Param("name") String name);

    @Query("select f from Friend f " +
            "where ((f.friendType = 'BLOCKED') or (f.friendType = 'BLOCKED_BY'))" +
            "and ((f.sender.id = :id and f.receiver.id = :friendId) or (f.sender.id = :friendId and f.receiver.id = :id))")
    Optional<Friend> findBlockedFriend(@Param("id") Long id, @Param("friendId") Long friendId);
}
