package kuchat.server.domain.message.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

import static kuchat.server.domain.message.service.MessageService.SERVER_ID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@ToString
public class ChatroomJoinRequest {

    @NotNull
    private Long chatroomId;

    @NotNull
    private Long senderId;

    @NotNull
    private String messageType;

    @NotNull
    private List<Long> memberIds;

    public ChatroomJoinRequest(Long chatroomId, List<Long> memberIds) {
        this.chatroomId = chatroomId;
        this.senderId = SERVER_ID;
        this.messageType = "JOIN";
        this.memberIds = memberIds;
    }

}
