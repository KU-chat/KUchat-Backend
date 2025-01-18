package kuchat.server.domain.member.dto;

import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.member.Member;
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
public class DetailProfileResponse extends BaseResponse {
    private String plusId;
    private LanguageResponse language;
    private ProfileResponse profile;


    public DetailProfileResponse(Member member) {
        this.responseStatus = BaseResponseStatus.SUCCESS;
        plusId = member.getPlusId();
        profile = new ProfileResponse(member.getProfile());
        language = new LanguageResponse(member.getLanguage());
    }
}
