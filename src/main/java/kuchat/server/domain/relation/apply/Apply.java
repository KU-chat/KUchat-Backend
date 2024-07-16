package kuchat.server.domain.relation.apply;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.relation.UnidirectRelation;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "apply")
@AttributeOverride(name = "id", column = @Column(name = "apply_id"))
public class Apply extends UnidirectRelation {

    public Apply(Member sender, Member receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }
}
