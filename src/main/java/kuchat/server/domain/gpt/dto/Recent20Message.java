package kuchat.server.domain.gpt.dto;

import jakarta.persistence.*;
import kuchat.server.domain.chatroom.Chatroom;
import kuchat.server.domain.enums.MessageType;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.message.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Recent20Message {
    private List<Message> message;

    private static class Message {
        private String senderName;
        private String text;
        private LocalDateTime createdDate;
    }
}
