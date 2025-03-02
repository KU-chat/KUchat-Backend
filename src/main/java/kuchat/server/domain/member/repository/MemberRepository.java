package kuchat.server.domain.member.repository;

import jakarta.persistence.LockModeType;
import kuchat.server.domain.enums.Platform;
import kuchat.server.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m " +
            "where m.platform = :platform and m.providerId = :providerId")
    Optional<Member> findByPlatformAndProviderId(@Param("platform") String platform,
                                                 @Param("providerId") String providerId);

    Optional<Member> findByStudentId(@Param("studentId") String studentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Member m where m.plusId = :plusId and m.status!='WITHDRAWN'")
    List<Member> findAllByPlusIdWithLock(@Param("plusId") String plusId);

    Optional<Member> findByPlusId(@Param("plusId") String plusId);
}
