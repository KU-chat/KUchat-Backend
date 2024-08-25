package kuchat.server.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kuchat.server.common.argumentResolver.Auth;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.ProfileResponse;
import kuchat.server.domain.member.dto.ProfileUpdateRequest;
import kuchat.server.domain.member.dto.SignupRequest;
import kuchat.server.domain.member.dto.SignupResponse;
import kuchat.server.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.stream.Collectors;

import static kuchat.server.common.exception.BaseResponse.INFO_BAD_REQUEST;
import static kuchat.server.common.exception.BaseResponse.SUCCESS;

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
    public ResponseEntity<SignupResponse> signup(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String guestToken,
                                                 @Validated @RequestBody SignupRequest signupRequest, BindingResult bindingResult) {
        log.info("[signup] 회원가입 요청");
        if(bindingResult.hasErrors()) {
            String messages = getErrorMessages(bindingResult);
            log.error("[signup] bindingResult messages = {}", messages);
            throw new KuchatException(INFO_BAD_REQUEST, messages);
        }

        String token = guestToken.trim().replace("Bearer ", "");
        log.info("[signup] guestToken = {}", token);
//        log.info("[signup] signupRequest = {}", signupRequest.toString());
        SignupResponse response = memberService.signup(token, signupRequest);
        return ResponseEntity.ok(response);
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
    public ResponseEntity<BaseResponse> updateMyProfile(@Auth Member member,
                                                        @Validated @RequestBody ProfileUpdateRequest requestBody,
                                                        BindingResult bindingResult) {
        log.info("[updateMyProfile] 프로필 수정 요청");
        if(bindingResult.hasErrors()) {
            String messages = getErrorMessages(bindingResult);
            log.error("[updateMyProfile] bindingResult messages = {}", messages);
            throw new KuchatException(INFO_BAD_REQUEST, messages);
        }
        log.info("[getMyProfile] 나의 프로필 수정 요청");
        memberService.updateProfile(member.getId(), requestBody);
        return ResponseEntity.ok(BaseResponse.SUCCESS);
    }

    @Operation(summary = "로그아웃")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout(@Auth Member member, HttpServletResponse response) throws IOException {
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
    public ResponseEntity<BaseResponse> quit(@Auth Member member) {
        log.info("[quit] memberId = {}", member.getId());
//        BaseResponse response = memberService.quit(memberId);
        BaseResponse response = BaseResponse.SUCCESS;
        return ResponseEntity.ok(response);
    }


    private static String getErrorMessages(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("$"));
    }
}
