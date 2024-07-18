package kuchat.server.domain.gpt.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GptRequest {
    private String gptModel;            // 필수
    private List<GptMessage> gptMessages;           // 필수
    private int temperature;
    private int topP;
    private int frequencyPenalty;
    private int presencePenalty;

}
