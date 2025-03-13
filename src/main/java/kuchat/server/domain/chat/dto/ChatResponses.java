package kuchat.server.domain.chat.dto;

import kuchat.server.domain.chat.Chat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Slf4j
@NoArgsConstructor
public class ChatResponses {

    private List<ChatResponse> chatResponses;

    public ChatResponses(List<Chat> chats) {
        chatResponses = chats.stream()
                .map(ChatResponse::new)
                .toList();
    }

    @Getter
    @Setter
    @ToString
    @Slf4j
    @NoArgsConstructor
    public static class ChatResponse {
        private String name;
        private String profileImage;
        private LocalDateTime lastSendTime;
//        private String homeTown;
//        private String recentMessage;
//        private int unreadMessageCount;

        public ChatResponse(Chat chat){
            this.name = chat.getName();
            this.profileImage = chat.getImage();
            this.lastSendTime = chat.getModifiedDate();
        }
    }
}
