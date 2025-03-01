package kuchat.server.domain.chat.dto;

import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class CreateChatResponse extends BaseResponse {
    private Long chatId;

    public CreateChatResponse(BaseResponseStatus responseStatus, Long chatId) {
        super(responseStatus);
        this.chatId = chatId;
    }
}
