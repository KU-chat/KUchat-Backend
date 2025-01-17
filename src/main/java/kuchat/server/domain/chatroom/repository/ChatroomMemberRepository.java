package kuchat.server.domain.chatroom.repository;

import kuchat.server.domain.chatroom.Chatroom;
import kuchat.server.domain.chatroom.ChatroomMember;
import kuchat.server.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatroomMemberRepository extends JpaRepository<ChatroomMember, Long> {

    ChatroomMember findByChatroomAndMember(Chatroom chatroom, Member member);

    List<ChatroomMember> findByChatroomId(Long chatroomId);
}
