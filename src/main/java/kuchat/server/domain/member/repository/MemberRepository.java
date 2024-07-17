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
            "where m.platform=:platform and m.attributeName=:attributeName")
    Optional<Member> findByPlatformAndAttributeName(@Param("platform") Platform platform, @Param("attributeName") String attributeName);

    List<Member> findAllByStudentId(String studentId);

    Optional<Member> findByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Member m where m.plusId = :plusId")
    List<Member> findAllByPlusIdWithLock(@Param("plusId") String plusId);

    @Query("select m from Member m where m.plusId = :plusId")
    Optional<Member> findByPlusId(@Param("plusId") String plusId);
}
