package kuchat.server.domain.chatroom;

import jakarta.persistence.*;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.BaseTime;
import kuchat.server.domain.enums.Status;
import kuchat.server.domain.message.Message;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "chatroom")
@Getter
@NoArgsConstructor
public class Chatroom extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

//    @OneToMany(mappedBy = "chatroom")
    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ChatroomMember> chatroomMembers = new HashSet<>();       // 채팅방에 속한 클라이언트들 리스트

    @OneToMany(mappedBy = "chatroom")
    private List<Message> messages = new ArrayList<>();


    @Builder
    public Chatroom(String name) {
        this.name = name;
        status = Status.ACTIVE;
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    public void addMember(ChatroomMember chatroomMember) {
        chatroomMembers.add(chatroomMember);
    }

    /**
     * ChatroomMembers HashSet에서 ChatroomMember 객체를 제거한 뒤, hashset의 size를 반환
     */
    public int deleteChatroomMember(ChatroomMember chatroomMember) {
        if (chatroomMembers.remove(chatroomMember)) {
            return chatroomMembers.size();
        } else {
            throw new KuchatException(BaseResponse.EXIT_CHATROOM_FAIL);
        }
    }
}
