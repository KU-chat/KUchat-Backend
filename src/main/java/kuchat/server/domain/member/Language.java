package kuchat.server.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kuchat.server.domain.enums.LearnLanguage;
import kuchat.server.domain.enums.SettingLanguage;
import kuchat.server.domain.member.dto.ProfileResponse;
import kuchat.server.domain.member.dto.ProfileUpdateRequest;
import kuchat.server.domain.member.dto.SignupRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Embeddable
@NoArgsConstructor
public class Language {
    @Column(name = "setting_language")
    @Enumerated(EnumType.STRING)
    private SettingLanguage setLanguage;

    @Column(name = "learn_language1")
    @Enumerated(EnumType.STRING)
    private LearnLanguage firstLanguage;

    @Column(name = "learn_language2")
    @Enumerated(EnumType.STRING)
    private LearnLanguage secondLanguage;

    public Language(SignupRequest request) {
        this.setLanguage = SettingLanguage.of(request.getSetLanguage());
        this.firstLanguage = LearnLanguage.of(request.getFirstLanguage());
        this.secondLanguage = LearnLanguage.of(request.getSecondLanguage());
    }

    public void update(ProfileUpdateRequest request) {
        firstLanguage = LearnLanguage.of(request.getFirstLanguage());
        secondLanguage = LearnLanguage.of(request.getSecondLanguage());
    }

    public void getLanguages(ProfileResponse response) {
        response.setFirstLanguage(firstLanguage.getValue());
        response.setSecondLanguage(secondLanguage.getValue());
    }
}
