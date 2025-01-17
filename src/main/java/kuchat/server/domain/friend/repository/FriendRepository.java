package kuchat.server.domain.friend.repository;

import kuchat.server.domain.friend.Friend;
import kuchat.server.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("select f from Friend f " +
            "where (f.follower = :member and f.followed.profile.name like %:name%)")
    List<Friend> findAllByName(@Param("member") Member member, @Param("name") String name);       // member 의 친구들 검색

    @Query("select f from Friend f " +
            "where (f.follower = :follower and f.followed = :followed)")
    Optional<Friend> findByMembers(@Param("follower") Member follower, @Param("followed") Member followed);
}
