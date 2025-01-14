package kuchat.server.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.jwt.JwtTokenService;
import kuchat.server.common.jwt.argumentResolver.Auth;
import kuchat.server.common.jwt.argumentResolver.Guest;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.Validator;
import kuchat.server.domain.enums.LearnLanguage;
import kuchat.server.domain.enums.SettingLanguage;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.ProfileResponse;
import kuchat.server.domain.member.dto.ProfileUpdateRequest;
import kuchat.server.domain.member.dto.SignupInfoResponse;
import kuchat.server.domain.member.dto.SignupRequest;
import kuchat.server.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static kuchat.server.common.response.BaseResponseStatus.NOT_FOUND_MEMBER;
import static kuchat.server.common.response.BaseResponseStatus.SUCCESS;

@Slf4j
@Tag(name = "Member", description = "회원")
@RequiredArgsConstructor
@RequestMapping("/member")
@RestController
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenService jwtTokenService;

    // 회원가입 처리하기
    @Operation(summary = "회원가입")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse> signup(@Guest Member member,
                                               @Validated @RequestBody SignupRequest signupRequest,
                                               BindingResult bindingResult) {
        log.info("[signup] 회원가입 요청");
        log.info("[signup] signupRequest = {}", signupRequest.toString());

        validateGuestToken(member);
        Validator.validateRequest(bindingResult);
        return memberService.signup(member, signupRequest);
    }

    @GetMapping("/signup")
    public ResponseEntity<BaseResponse> signup(@RequestParam("guest-token") String token) {
        log.info("[signup] 토큰이 없어도 회원가입 페이지로 이동 가능");
        List<String> languages = Arrays.stream(LearnLanguage.values())
                .map(LearnLanguage::getValue)
                .toList();

        List<String> settingLanguages = SettingLanguage.getValues();
        SignupInfoResponse response = new SignupInfoResponse(token, languages, settingLanguages);

        return ResponseEntity.ok(response);
    }

    private static void validateGuestToken(Member member) {
        if (member == null) {
            log.error("[signup] guest token을 가지고 찾은 멤버가 null인 오류");
            throw new KuchatException(NOT_FOUND_MEMBER);
        }
    }

    @Operation(summary = "나의 프로필 조회")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/my-profile")
    public ResponseEntity<ProfileResponse> getMyProfile(@Auth Member member) {
        log.info("[getMyProfile] 나의 프로필 조회 요청 memberId = {}", member.getId());
        ProfileResponse response = memberService.getProfile(member);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "나의 프로필 수정")
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/my-profile")
    public ResponseEntity<BaseResponseStatus> updateMyProfile(@Auth Member member,
                                                              @Validated @RequestBody ProfileUpdateRequest requestBody,
                                                              BindingResult bindingResult) {
        log.info("[updateMyProfile] 프로필 수정 요청");
        Validator.validateRequest(bindingResult);
        log.info("[getMyProfile] 수정할 프로필 기록 = {}", requestBody.toString());
        memberService.updateProfile(member.getId(), requestBody);
        return ResponseEntity.ok(BaseResponseStatus.SUCCESS);
    }

    @Operation(summary = "로그아웃")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/logout")
    public ResponseEntity<BaseResponseStatus> logout(@Auth Member member, HttpServletResponse response) throws IOException {
        log.info("[logout] memberId = {}", member.getId());
        memberService.logout(member);

        // 클라이언트 쿠키에서 토큰 제거
        Cookie cookie = new Cookie("refresh-token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        response.sendRedirect("/");
        return ResponseEntity.ok(SUCCESS);
    }

    @Operation(summary = "회원 탈퇴")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/quit")
    public ResponseEntity<BaseResponseStatus> quit(@Auth Member member) {
        log.info("[quit] memberId = {}", member.getId());
        BaseResponseStatus response = memberService.quit(member);
        return ResponseEntity.ok(response);
    }

}
