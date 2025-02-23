package kuchat.server.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatRequest {
    @NotEmpty
    private List<Long> friends;

    @NotBlank
    private String name;        // 사용자가 값을 입력하지 않은 경우 프론트에서 사용자들의 이름 나열로 입력해서 요청보내기
}
