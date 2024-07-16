package kuchat.server.domain.relation.sentRequest;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kuchat.server.domain.relation.UnidirectRelation;

@Entity
@Table(name = "sent_request")
@AttributeOverride(name = "id", column = @Column(name = "sent_request_id"))
public class SentRequest extends UnidirectRelation {

}
