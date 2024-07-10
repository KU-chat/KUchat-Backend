package kuchat.server.domain.message.dto;

import kuchat.server.domain.enums.MessageType;
import lombok.*;

import static kuchat.server.domain.message.service.MessageService.SERVER_ID;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChatroomLeaveRequest extends ChatMessage{
    private Long memberId;
    private Long chatroomId;
    private Long senderId = SERVER_ID;
    private MessageType messageType = MessageType.LEAVE;
    private String text;

    @Builder
    public ChatroomLeaveRequest(Long chatroomId, Long memberId, String text) {
        this.chatroomId = chatroomId;
        this.memberId = memberId;
        this.text = text;
    }
}
