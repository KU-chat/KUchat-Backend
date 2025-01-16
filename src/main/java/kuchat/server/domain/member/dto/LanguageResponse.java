package kuchat.server.domain.member.dto;

import kuchat.server.domain.member.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@ToString
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class LanguageResponse {
    private String firstLanguage;
    private String secondLanguage;

    public LanguageResponse(Language language) {
        firstLanguage = language.getFirstLanguage().getValue();
        secondLanguage = language.getSecondLanguage().getValue();
    }
}
