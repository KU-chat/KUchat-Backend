package kuchat.server.domain.chatroom;

import jakarta.persistence.*;
import kuchat.server.domain.BaseTime;
import kuchat.server.domain.enums.Status;
import kuchat.server.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;

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
    private Status status;

    @OneToMany(mappedBy = "chatroom")
    private HashSet<ChatroomMember> chatroomMembers = new HashSet<>();       // 채팅방에 속한 클라이언트들 리스트


//    @OneToMany(mappedBy = "room")
//    private List<Message> messages = new ArrayList<>();


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
}
