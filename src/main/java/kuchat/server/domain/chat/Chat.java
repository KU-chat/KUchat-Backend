package kuchat.server.domain.chat;

import jakarta.persistence.*;
import kuchat.server.domain.BaseTime;
import kuchat.server.domain.enums.ChatState;
import kuchat.server.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Chat extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member1_id")
    private Member member1;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member2_id")
    private Member member2;

    @Enumerated(value = EnumType.STRING)
    private ChatState state;

    public Chat(Member member1, Member member2){
        this.member1 = member1;
        this.member2 = member2;
        this.state = ChatState.ACTIVE;
    }
}
