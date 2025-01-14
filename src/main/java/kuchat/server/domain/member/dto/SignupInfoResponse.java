package kuchat.server.domain.member.dto;

import kuchat.server.common.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SignupInfoResponse extends BaseResponse {
    private String guestToken;
    private List<String> languages;
    private List<String> setLanguages;
}
