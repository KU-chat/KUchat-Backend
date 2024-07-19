package kuchat.server.domain.gpt.dto;

import kuchat.server.domain.message.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RecentMessage {
    private String senderName;
    private String text;
    private LocalDateTime createdDate;

    public RecentMessage (String senderName, Message message) {
        this.senderName = senderName;
        this.text = message.getText();
        this.createdDate = message.getCreatedDate();
    }
}
