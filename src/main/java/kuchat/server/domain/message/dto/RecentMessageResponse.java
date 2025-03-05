package kuchat.server.domain.message.dto;

import kuchat.server.domain.message.Message;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 채팅방 화면을 조회하는 API의 응답인 ViewChatResponse 내에서 사용하기 때문에 BaseResponse를 상속받지 않는다.
 */
@Getter
@Setter
@ToString
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class RecentMessageResponse {
    private Long senderId;
    private LocalDateTime sendTime;
    private String content;

    public RecentMessageResponse(Message message) {
        this.senderId = message.getSenderId();
        this.sendTime = message.getCreatedDate();
        this.content = message.getContent();
    }

}
