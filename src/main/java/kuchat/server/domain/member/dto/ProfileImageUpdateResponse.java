package kuchat.server.domain.member.dto;

import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
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
public class ProfileImageUpdateResponse extends BaseResponse {
    private String newProfileImage;

    public ProfileImageUpdateResponse(BaseResponseStatus baseResponseStatus, String newProfile) {
        super(baseResponseStatus);
        this.newProfileImage = newProfile;
    }
}
