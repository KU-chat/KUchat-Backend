package kuchat.server.domain.chat.dto;

import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.function.LongConsumer;

@Getter
@ToString
@NoArgsConstructor
public class CreateChatResponse extends BaseResponse {
    private Long chatId;

    public CreateChatResponse(BaseResponseStatus responseStatus, Long chatId){
        super(responseStatus);
        this.chatId = chatId;
    }
}
