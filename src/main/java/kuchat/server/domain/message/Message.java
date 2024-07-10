package kuchat.server.domain.message;

import jakarta.persistence.*;
import kuchat.server.domain.BaseTime;
import kuchat.server.domain.chatroom.Chatroom;
import kuchat.server.domain.enums.MessageType;
import kuchat.server.domain.message.dto.ChatMessage;
import lombok.*;

@Entity
@Table(name = "message")
@Getter
@NoArgsConstructor
@ToString
public class Message extends BaseTime {

    @Setter
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "messageId", column = @Column(name = "message_id")),
            @AttributeOverride(name = "chatroomId", column = @Column(name = "chatroom_id", insertable = false, updatable = false))
    })
    private MessageId messageId;

    @Column(name = "generated_message_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_id_seq")
    @SequenceGenerator(name = "message_id_seq", sequenceName = "message_id_seq", allocationSize = 1)
    private Long generatedMessageId;

    // 메시지는 하나의 채팅방 안에서만 고유하도록 id 생성
    @MapsId("chatroomId")       // MessageId 클래스에 있는 chatroomId 변수와 매핑
    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "parent_message_id", referencedColumnName = "message_id"),
            @JoinColumn(name = "parent_chatroom_id", referencedColumnName = "chatroom_id"/*, insertable=false, updatable=false*/)
    })
    private Message parent = null;       // chatroomId 없이 messageId만 가지면 된다.

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "message_type")
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    private String text;

    @Builder
    public Message(Chatroom chatroom, Long senderId, MessageType messageType, String text) {
        this.chatroom = chatroom;
        this.senderId = senderId;
        this.messageType = messageType;
        this.text = text;
    }

    public Message(ChatMessage chatMessage, Chatroom chatroom) {
        this.messageId = new MessageId(generatedMessageId, chatMessage.getChatroomId());
        this.chatroom = chatroom;
        this.messageType = chatMessage.getMessageType();
        this.senderId = chatMessage.getSenderId();
        this.text = chatMessage.getText();
    }


    // 답장, 번역 메세지처럼 부모가 있는 메세지에 대한 생성자
    public Message(ChatMessage chatMessage, Chatroom chatroom, Message parent) {
        this.chatroom = chatroom;
        this.messageType = chatMessage.getMessageType();
        this.senderId = chatMessage.getSenderId();
        this.parent = parent;
        this.text = chatMessage.getText();
    }

}
