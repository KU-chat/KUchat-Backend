package kuchat.server.domain.friend.repository;

import kuchat.server.domain.friend.Friend;
import kuchat.server.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("select f from Friend f " +
            "join fetch f.receiver r " +
            "where f.sender = :member and " +
            "(:name = '' or r.profile.name like '%' || :name || '%')")
    Page<Friend> findBySenderAndReceiver_Name(@Param("member") Member member,
                                            @Param("name") String name,
                                            Pageable pageable);

    @Query("select f from Friend f " +
            "where ((f.sender.id = :senderId and f.receiver.id = :receiverId) " +
            "or (f.sender.id = :receiverId and f.receiver.id = :senderId)) " +
            "and (f.acceptance = :acceptance)")
    Optional<Friend> findByMembersAndAcceptance(@Param("senderId") Long senderId,
                                                @Param("receiverId") Long receiverId,
                                                @Param("acceptance") boolean acceptance);

    @Query("select f from Friend f " +
            "where ((f.sender.id = :senderId and f.receiver.id = :receiverId) " +
            "or (f.sender.id = :receiverId and f.receiver.id = :senderId))")
    Optional<Friend> findByMembers(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    Optional<Friend> findBySenderAndReceiver(Member sender, Member receiver);

    Optional<Friend> findByIdAndReceiver_IdAndAcceptance(Long friendId, Long memberId, boolean acceptance);

    @Query("select f from Friend f " +
            "join fetch f.sender s " +
            "where f.receiver = :receiver and f.acceptance = :acceptance")
    Page<Friend> findAllByReceiverAndAcceptance(Member receiver, boolean acceptance, Pageable pageable);


//    @Query("select f from Friend f " +
//            "where (f.sender = :sender and f.receiver = :receiver) " +
//            "or (f.sender = :receiver and f.receiver = :sender)")
//    Optional<Friend> findFirstByMembers(Member member, Member friendMember);
}
