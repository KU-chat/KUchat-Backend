package kuchat.server.domain.member.repository;

import jakarta.persistence.LockModeType;
import kuchat.server.domain.enums.Platform;
import kuchat.server.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m " +
            "where m.platform=:platform and m.providerId=:providerId and m.status!='WITHDRAWN'")
    Optional<Member> findByPlatformAndProviderId(@Param("platform") Platform platform,
                                                 @Param("providerId") String providerId);

    @Query("select m from Member m where m.studentId = :studentId and m.status != 'WITHDRAWN'")
    List<Member> findAllByStudentId(@Param("studentId") String studentId);

    @Query("select m from Member m where m.email = :email and m.status != 'WITHDRAWN'")
    Optional<Member> findByEmail(@Param("email") String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Member m where m.plusId = :plusId and m.status!='WITHDRAWN'")
    List<Member> findAllByPlusIdWithLock(@Param("plusId") String plusId);

    @Query("select m from Member m where m.plusId = :plusId and m.status!='WITHDRAWN'")
    Optional<Member> findByPlusId(@Param("plusId") String plusId);
}
