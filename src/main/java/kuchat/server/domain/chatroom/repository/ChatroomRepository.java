package kuchat.server.domain.chatroom.repository;

import kuchat.server.domain.chatroom.Chatroom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    @Query("select c from Chatroom c where c.name like %:name%")
    Page<Chatroom> findByName(@Param("name") String name, Pageable pageable);
}
