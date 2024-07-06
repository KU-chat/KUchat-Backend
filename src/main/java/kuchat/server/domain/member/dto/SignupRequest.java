package kuchat.server.domain.member.dto;

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
    private String platform;
    private String attributeName;

    private String setLanguage;          // SettingLanguage 타입
    private String firstLanguage;         // LearnLanguage
    private String secondLanguage;       // LearnLanguage
    private String hometown;
    private String name;
    private String department;
    private String studentId;
    private String gender;
    private String birthday;
    private String email;
}
