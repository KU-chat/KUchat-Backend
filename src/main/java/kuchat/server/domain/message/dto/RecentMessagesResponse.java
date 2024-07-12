package kuchat.server.domain.message.dto;

import kuchat.server.domain.message.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@ToString
public class RecentMessagesResponse {
    private List<MessageResponse> recentMessages;

    public RecentMessagesResponse(List<Message> messages) {
        List<MessageResponse> messageResponses = messages.stream()
                .map(MessageResponse::new)
                .collect(Collectors.toList());
        this.recentMessages = messageResponses;
    }
}
