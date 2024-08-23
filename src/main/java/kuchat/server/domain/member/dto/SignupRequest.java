package kuchat.server.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import kuchat.server.domain.enums.Platform;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SignupRequest {

    @NotBlank
    private String setLanguage;          // SettingLanguage 타입

    @NotBlank
    private String firstLanguage;         // LearnLanguage

    @NotBlank
    private String secondLanguage;       // LearnLanguage

    @NotBlank
    private String hometown;

    @Pattern(regexp = "[a-zA-Z0-9]{4,20}$", message = "이름은 최소 4자 이상, 20자 이하이며, 영문과 숫자만 입력하세요.")
    private String name;

    @NotBlank
    private String department;

    @Pattern(regexp = "^[0-9]{9}$", message = "학번은 9자리 숫자여야 합니다.")
    private String studentId;

    @NotBlank
    private String gender;

    @Pattern(regexp = "^[0-9]{6}$", message = "생일은 6자리 숫자여야 합니다.")
    private String birthday;
}
