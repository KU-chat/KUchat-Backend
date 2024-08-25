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
public class Message extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;

    @ManyToOne
    @JoinColumn(name = "parent_message_id", referencedColumnName = "message_id")
    private Message parent = null;       // chatroomId 없이 messageId만 가지면 된다.

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "message_type")
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    private String text;

    public Message(ChatMessage chatMessage, Chatroom chatroom) {
        this.chatroom = chatroom;
        this.messageType = MessageType.valueOf(chatMessage.getMessageType());
        this.senderId = chatMessage.getSenderId();
        this.text = chatMessage.getText();
    }


    // 답장, 번역 메세지처럼 부모가 있는 메세지에 대한 생성자
    public Message(ChatMessage chatMessage, Chatroom chatroom, Message parent) {
        this.chatroom = chatroom;
        this.messageType = MessageType.valueOf(chatMessage.getMessageType());
        this.senderId = chatMessage.getSenderId();
        this.parent = parent;
        this.text = chatMessage.getText();
    }

}
