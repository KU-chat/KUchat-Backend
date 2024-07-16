package kuchat.server.domain.relation;

import jakarta.persistence.*;
import kuchat.server.domain.BaseTime;
import kuchat.server.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Getter
@NoArgsConstructor
public abstract class UnidirectRelation extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id")
    protected Member sender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "receiver_id")
    protected Member receiver;
}
