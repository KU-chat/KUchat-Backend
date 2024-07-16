package kuchat.server.domain.relation.block;

import jakarta.persistence.*;
import kuchat.server.domain.relation.UnidirectRelation;

@Entity
@Table(name = "block_member")
@AttributeOverride(name = "id", column = @Column(name = "block_id"))
public class Block extends UnidirectRelation {

}
