package kuchat.server.domain.member.dto;

import kuchat.server.domain.member.Member;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@ToString
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class DetailProfileResponse {
    private Long id;
    private String plusId;

    private LanguageResponse language;
    private ProfileResponse profile;


    public DetailProfileResponse(Member member) {
        this.id = member.getId();
        plusId = member.getPlusId();
        profile = new ProfileResponse(member.getProfile());
        language = new LanguageResponse(member.getLanguage());
    }
}
