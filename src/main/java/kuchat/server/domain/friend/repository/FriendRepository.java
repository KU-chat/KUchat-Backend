package kuchat.server.domain.friend.repository;

import kuchat.server.domain.friend.Friend;
import kuchat.server.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("select f from Friend f " +
            "where f.member1.id = :id or f.member2.id = :id")
    List<Friend> findAllByMemberId(@Param("id") Long id);

    @Query("select f from Friend f " +
            "where (f.member1.id = :id1 and f.member2.id = :id2) or " +
            "(f.member1.id = :id2 and f.member2.id = :id1)")
    Optional<Friend> findByMemberIds(Long id1, Long id2);

    @Query("select f from Friend f " +
            "where ((f.member1 = :member and f.member2.name like %:name%) or " +
            "(f.member2 = :member and f.member1.name like %:name%))")
    List<Friend> findAllByName(@Param("member")Member member, @Param("name") String name);       // member 의 친구들 검색

    @Query("select f from Friend f " +
            "where (f.member1 = :m1 and f.member2 = :m2) or " +
            "(f.member1 = :m2 and f.member2 = :m1)")
    Optional<Friend> findByMembers(@Param("m1") Member member1, @Param("m2") Member member2);
}
