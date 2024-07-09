package kuchat.server.domain.message.dto;

import kuchat.server.domain.message.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@ToString
public class RecentMessagesResponse {
    private List<ChatMessage> recentMessages;

    public RecentMessagesResponse(List<Message> messages) {
        List<ChatMessage> chatMessages = messages.stream()
                .map(message -> new ChatMessage(message))
                .collect(Collectors.toList());
        this.recentMessages = chatMessages;
    }
}
