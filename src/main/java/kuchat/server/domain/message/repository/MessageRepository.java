package kuchat.server.domain.message.repository;

import kuchat.server.domain.chatroom.Chatroom;
import kuchat.server.domain.message.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("select m from Message m where m.chatroom = :chatroom order by m.createdDate DESC")
    List<Message> findRecent20MessagesByChatroomId(@Param("chatroom") Chatroom chatroom, Pageable pageable);
}
