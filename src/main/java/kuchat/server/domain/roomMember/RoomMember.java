package kuchat.server.domain.roomMember;

import jakarta.persistence.*;
import kuchat.server.domain.BaseTime;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.chatroom.Chatroom;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "room_member")
@Getter
@NoArgsConstructor

public class RoomMember extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_member_id", nullable = false)
    private Long roomId;

    @ManyToOne              // member : roomMember = 1:다 -> roomMember에는 ManyToOne
    @JoinColumn(name = "member_id")         // fk 이름이 member_id가 된다. 얘가 연관관계 주인
    private Member member;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Chatroom chatroom;
}
