package kuchat.server.domain.chat.dto;

import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.chat.Chat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Getter
@Setter
@ToString
@Slf4j
@NoArgsConstructor
public class ViewChatResponse extends BaseResponse {
    private Long chatId;
    private String chatProfile;
    private String chatName;        // 채팅방 이름
    private List<RecentMessageResponse> recentMessages;

    public ViewChatResponse(BaseResponseStatus responseStatus, Chat chat){
        super(responseStatus);
        this.chatId = chat.getId();
        this.chatProfile = chat.getImage();
        this.chatName = chat.getName();
    }
}
