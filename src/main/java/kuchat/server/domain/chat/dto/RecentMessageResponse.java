package kuchat.server.domain.chat.dto;

import kuchat.server.domain.member.Member;
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
    private Long messageId;
    private SenderResponse sender;
    private LocalDateTime sendTime;
    private String content;

    public RecentMessageResponse(Message message) {
        this.messageId = message.getId();
        this.sender = new SenderResponse(message.getSender());
        this.sendTime = message.getCreatedDate();
        this.content = message.getContent();
    }

    @Getter
    @Setter
    @ToString
    @Slf4j
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SenderResponse {

        private Long id;
        private String name;
        private String profileImage;

        public SenderResponse(Member sender) {
            this.id = sender.getId();
            this.name = sender.getName();
            this.profileImage = sender.getProfile().getProfileImage();
        }
    }

}
