package kuchat.server.domain.friend;

import jakarta.persistence.*;
import kuchat.server.domain.BaseTime;
import kuchat.server.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "friendship")
@NoArgsConstructor
@Getter
public class Friend extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member1_id")
    private Member member1;

    @ManyToOne
    @JoinColumn(name = "member2_id")
    private Member member2;


    public Friend(Member member1, Member member2) {
        this.member1 = member1;
        this.member2 = member2;
    }

    public boolean equals(Friend friend) {
        if (Objects.equals(this.member1, friend.getMember1()) &&
                Objects.equals(this.member2, friend.getMember2())) {
            return true;
        } else if (Objects.equals(this.member1, friend.getMember2()) &&
                Objects.equals(this.member2, friend.getMember1())) {
            return true;
        }
        return false;
    }

}
