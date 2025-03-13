package kuchat.server.domain.chat.dto;

import kuchat.server.domain.chat.ChatMember;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 채팅방 화면을 조회하는 API의 응답인 ViewChatResponse 내에서 사용하기 때문에 BaseResponse를 상속받지 않는다.
 */
@Getter
@Setter
@ToString
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ChatMemberResponse {
    private Long memberId;
    private String name;
    private String profileImage;

    public ChatMemberResponse(ChatMember chatMember) {
        this.memberId = chatMember.getMember().getId();
        this.name = chatMember.getMember().getName();
        this.profileImage = chatMember.getMember().getProfile().getProfileImage();
    }
}
