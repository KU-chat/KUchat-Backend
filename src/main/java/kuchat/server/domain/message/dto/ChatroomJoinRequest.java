package kuchat.server.domain.message.dto;

import kuchat.server.domain.enums.MessageType;
import lombok.*;

import static kuchat.server.domain.message.service.MessageService.SERVER_ID;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChatroomJoinRequest extends ChatMessage{
    private Long memberId;
    private Long chatroomId;
    private Long senderId = SERVER_ID;
    private MessageType messageType = MessageType.JOIN;
    private String text = null;

    @Builder
    public ChatroomJoinRequest(Long chatroomId, Long memberId) {
        this.chatroomId = chatroomId;
        this.memberId = memberId;
    }
}
