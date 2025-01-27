package kuchat.server.domain.friend;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kuchat.server.domain.BaseTime;
import kuchat.server.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "friend")
@NoArgsConstructor
@Getter
public class Friend extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")            // 팔로우한 사람
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")           // 팔로우 당한 사람
    private Member receiver;

    @Column(nullable = false)
    private boolean acceptance = false;

    @Builder
    public Friend(Member sender, Member receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public void accept(){
        this.acceptance = true;
    }

}
