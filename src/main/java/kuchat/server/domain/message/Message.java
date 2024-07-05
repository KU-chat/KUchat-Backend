package kuchat.server.domain.message;

import jakarta.persistence.*;
import kuchat.server.domain.BaseTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "message")
@Getter
@NoArgsConstructor
@ToString
public class Message extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;
    // 메시지는 하나의 채팅방 안에서만 고유하도록 설정

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "parent_id")
    private Long parentMessageId;

    @Column(name = "sender_id")
    private Long sender;
}
