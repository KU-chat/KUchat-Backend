package kuchat.server.domain.chatroom;

import jakarta.persistence.*;
import kuchat.server.domain.BaseTime;
import kuchat.server.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chatroom_member")
@Getter
@NoArgsConstructor
public class ChatroomMember extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_member_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)             // member : roomMember = 1:다 -> roomMember에는 ManyToOne
    @JoinColumn(name = "member_id")         // fk 이름이 member_id가 된다. 얘(Member)가 연관관계 주인
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;

    public ChatroomMember(Member member, Chatroom chatroom){
        this.member = member;
        this.chatroom = chatroom;
    }

}
