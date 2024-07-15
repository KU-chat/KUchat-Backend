package kuchat.server.domain.friend;

import jakarta.persistence.*;
import kuchat.server.domain.BaseTime;
import kuchat.server.domain.enums.FriendType;
import kuchat.server.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "friendship")
@Getter
@NoArgsConstructor
public class Friend extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friendship_id")
    private Long id;

    @ManyToOne(optional = false)        // 반드시 존재해야 함.
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne(optional = false)        // 반드시 존재해야 함.
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "friend_type")
    private FriendType friendType;

    public Friend(Member sender, Member receiver, FriendType friendType) {
        this.sender = sender;
        this.receiver = receiver;
        this.friendType = friendType;
    }

    public void accept() {
        friendType = FriendType.FRIEND;
    }

    public boolean equals(Friend friend) {
        if (Objects.equals(this.sender.getId(), friend.getSender().getId()) &&
                Objects.equals(this.receiver.getId(), friend.getReceiver().getId())) {
            return true;
        } else if (Objects.equals(this.sender.getId(), friend.getReceiver().getId()) &&
                Objects.equals(this.receiver.getId(), friend.getSender().getId())) {
            return true;
        }
        return false;

    }

}
