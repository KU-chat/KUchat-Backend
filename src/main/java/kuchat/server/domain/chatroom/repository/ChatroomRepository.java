package kuchat.server.domain.chatroom.repository;

import kuchat.server.domain.chatroom.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    List<Chatroom> findByNameLike(String name);
}
