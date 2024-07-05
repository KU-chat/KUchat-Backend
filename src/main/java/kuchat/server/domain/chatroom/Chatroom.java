package kuchat.server.domain.chatroom;

import kuchat.server.domain.BaseTime;
import kuchat.server.domain.enums.Status;
import jakarta.persistence.*;
import kuchat.server.domain.roomMember.RoomMember;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    private List<RoomMember> roomMember = new ArrayList<>();

//    @OneToMany(mappedBy = "room")
//    private List<Message> messages = new ArrayList<>();
}
