package kuchat.server.domain.message;

import jakarta.persistence.*;
import kuchat.server.domain.BaseTime;
import kuchat.server.domain.chat.Chat;
import kuchat.server.domain.enums.MessageType;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.message.dto.MessageRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "message")
@Getter
@NoArgsConstructor
public class Message extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "parent_message_id", referencedColumnName = "message_id")
//    private Message parent = null;       // chatroomId 없이 messageId만 가지면 된다.

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @Column(name = "message_type")
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    private String content;

    public Message(MessageRequest messageRequest, Chat chat, Member sender) {
        this.chat = chat;
        this.messageType = messageRequest.getMessageType();
        this.sender = sender;
        this.content = messageRequest.getContent();
    }

}
