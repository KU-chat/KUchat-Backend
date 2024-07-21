package kuchat.server.domain.message.dto;

import kuchat.server.domain.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChatroomJoinRequest extends ChatMessage {
    private Long chatroomId;
    private List<Long> memberIds;
    private MessageType messageType = MessageType.JOIN;

    public ChatroomJoinRequest(Long chatroomId, List<Long> memberIds) {
        this.chatroomId = chatroomId;
        this.memberIds = memberIds;
    }
}
