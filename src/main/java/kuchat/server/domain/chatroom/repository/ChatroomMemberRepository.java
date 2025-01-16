package kuchat.server.domain.chatroom.repository;

import kuchat.server.domain.chatroom.Chatroom;
import kuchat.server.domain.chatroom.ChatroomMember;
import kuchat.server.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatroomMemberRepository extends JpaRepository<ChatroomMember, Long> {
    @Query("select cm " +
            "from ChatroomMember cm " +
            "where cm.chatroom=:chatroom and cm.member=:member")
    ChatroomMember findByChatroomAndMember(@Param("chatroom") Chatroom chatroom, @Param("member") Member member);


    @Query("select cm " +
            "from ChatroomMember cm " +
            "where cm.chatroom.id = :chatroomId")
    List<ChatroomMember> findByChatroomId(@Param("chatroomId") Long chatroomId);
}
