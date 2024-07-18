package kuchat.server.domain.gpt.dto;

import lombok.*;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GptResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private int index;      // gpt 대화 인덱스 번호
        private GptMessage gptMessage;         // gpt로부터 받은 메세지,
    }
}
