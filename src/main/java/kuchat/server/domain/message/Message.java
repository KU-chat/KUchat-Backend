package kuchat.server.domain.message;

import jakarta.persistence.*;
import kuchat.server.domain.BaseTime;
import kuchat.server.domain.chatroom.Chatroom;
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
    @Column(name = "message_id")
    private MessageId messageId;

    // 메시지는 하나의 채팅방 안에서만 고유하도록 id 생성
    @MapsId("chatroomId")       // MessageId 클래스에 있는 chatroomId 변수와 매핑
    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;

    @Column(name = "parent_id")
    @JoinColumns({
            @JoinColumn(name = "chatroom_id"),
            @JoinColumn(name = "message_id")
    })
    private MessageId parentMessageId;

    @Column(name = "sender_id")
    private Long sender;


    private String text;
}
