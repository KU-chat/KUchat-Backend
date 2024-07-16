package kuchat.server.domain.relation.receivedRequest;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kuchat.server.domain.relation.UnidirectRelation;

@Entity
@Table(name = "block_member")
@AttributeOverride(name = "id", column = @Column(name = "received_request_id"))
public class ReceivedRequest extends UnidirectRelation {

}
