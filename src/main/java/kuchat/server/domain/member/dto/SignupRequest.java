package kuchat.server.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SignupRequest {

    @NotBlank
    private String setLanguage;          // SettingLanguage 타입

    @NotBlank
    private String firstLanguage;         // LearnLanguage

    @NotBlank
    private String secondLanguage;       // LearnLanguage

    @NotBlank
    private String hometown;

    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,20}$", message = "이름은 최소 2자 이상, 20자 이하이며, 영문/한글/숫자만 입력 가능합니다.")
    private String name;

    @NotBlank
    private String department;

    @Pattern(regexp = "^[0-9]{9}$", message = "학번은 9자리 숫자여야 합니다.")
    private String studentId;

    @NotBlank
    private String gender;

    private LocalDate birthday;
}
