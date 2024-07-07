package kuchat.server.domain.message;

import jakarta.persistence.*;
import kuchat.server.domain.BaseTime;
import kuchat.server.domain.chatroom.Chatroom;
import kuchat.server.domain.enums.MessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "message")
@Getter
@NoArgsConstructor
@ToString
public class Message extends BaseTime {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name="messageId", column=@Column(name="message_id")),
            @AttributeOverride(name="chatroomId", column=@Column(name="chatroom_id", insertable=false, updatable=false))
    })
    private MessageId messageId;

    // 메시지는 하나의 채팅방 안에서만 고유하도록 id 생성
    @MapsId("chatroomId")       // MessageId 클래스에 있는 chatroomId 변수와 매핑
    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name="parent_message_id", referencedColumnName="message_id"),
            @JoinColumn(name="parent_chatroom_id", referencedColumnName="chatroom_id"/*, insertable=false, updatable=false*/)
    })
    private Message parent;       // chatroomId 없이 messageId만 가지면 된다.

    @Column(name = "sender_id")
    private Long sender;

    @Column(name = "message_type")
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    private String text;
}
