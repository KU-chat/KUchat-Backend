package kuchat.server.domain.friend;

import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "friend_member_id")
    private Member followed;

    @ManyToOne
    @JoinColumn(name = "friend_member_id")
    private Member follower;


    @Builder
    public Friend(Member follower, Member followed) {
        this.follower = follower;
        this.followed = followed;
    }

}
