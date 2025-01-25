package kuchat.server.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProfileUpdateRequest {

    @NotBlank
    private String firstLanguage;

    @NotBlank
    private String secondLanguage;

    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,20}$", message = "이름은 최소 2자 이상, 20자 이하이며, 영문/한글/숫자만 입력 가능합니다.")
    private String name;

    @Pattern(regexp = "^[a-zA-Z0-9]{7,20}$", message = "plusId는 최소 7자 이상, 20자 이하이며, 영문과 숫자만 입력 가능합니다.")
    private String plusId;

    @NotBlank
    private String department;

    private String aboutMe;

}
