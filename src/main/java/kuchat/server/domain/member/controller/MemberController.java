package kuchat.server.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.auth.argumentResolver.Auth;
import kuchat.server.domain.auth.argumentResolver.Guest;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.domain.utils.CookieUtil;
import kuchat.server.domain.utils.ValidatorUtil;
import kuchat.server.domain.auth.dto.AuthTokenResponse;
import kuchat.server.domain.enums.LearnLanguage;
import kuchat.server.domain.enums.SettingLanguage;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.SignupInfoResponse;
import kuchat.server.domain.member.dto.SignupRequest;
import kuchat.server.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static kuchat.server.common.response.BaseResponseStatus.NOT_FOUND_MEMBER;

@Slf4j
@Tag(name = "Member", description = "회원")
@RequiredArgsConstructor
@RequestMapping("/member")
@RestController
public class MemberController {

    private final MemberService memberService;

    // 회원가입 처리하기
    @Operation(summary = "회원가입")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse> signup(@Guest Member member,
                                               HttpServletResponse response,
                                               @Validated @RequestBody SignupRequest signupRequest,
                                               BindingResult bindingResult) {
        log.info("[signup] 회원가입 요청 signupRequest = {}", signupRequest.toString());
        validateGuestToken(member);
        ValidatorUtil.validateRequest(bindingResult);
        AuthTokenResponse tokenResponse = memberService.signup(member, signupRequest, response);
        return CookieUtil.setAuthToken(tokenResponse);
    }

    private void validateGuestToken(Member member) {
        if (member == null) {
            log.error("[signup] guest token을 가지고 찾은 멤버가 null인 오류");
            throw new KuchatException(NOT_FOUND_MEMBER);
        }
    }

    @Operation(summary = "회원 탈퇴")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/quit")
    public ResponseEntity<BaseResponse> quit(@Auth Member member) {
        log.info("[quit] memberId = {}", member.getId());
        memberService.quit(member);
        return CookieUtil.removeAuthToken();
    }

}
