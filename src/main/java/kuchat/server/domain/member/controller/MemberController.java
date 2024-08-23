package kuchat.server.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.common.argumentResolver.Auth;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.member.dto.ProfileResponse;
import kuchat.server.domain.member.dto.ProfileUpdateRequest;
import kuchat.server.domain.member.dto.SignupRequest;
import kuchat.server.domain.member.dto.SignupResponse;
import kuchat.server.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static kuchat.server.common.exception.BaseResponse.DUPLICATED_STUDENT_ID;

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
    public ResponseEntity<SignupResponse> signup(@RequestHeader(HttpHeaders.AUTHORIZATION) String guestToken,
                                                 @Validated @RequestBody SignupRequest signupRequest) {
        String token = guestToken.trim().replace("Bearer ", "");
        log.info("[signup] guestToken = {}", token);
        log.info("[signup] signupRequest = {}", signupRequest.toString());
        if (memberService.duplicateStudentId(signupRequest.getStudentId())) {
            log.error("[error] 이미 존재하는 학번입니다.");
            throw new KuchatException(DUPLICATED_STUDENT_ID);
        }
        SignupResponse response = memberService.signup(token, signupRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "나의 프로필 조회")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/my-profile")
    public ResponseEntity<ProfileResponse> getMyProfile(@Auth Long memberId) {
        log.info("[getMyProfile] 나의 프로필 조회 요청");
        log.info("[getMyProfile] memberId = {}", memberId);
        ProfileResponse response = memberService.getProfile(memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "나의 프로필 수정")
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/my-profile")
    public ResponseEntity<BaseResponse> updateMyProfile(@Auth Long memberId,
                                                        @RequestBody ProfileUpdateRequest requestBody) {
        log.info("[getMyProfile] 나의 프로필 수정 요청");
        memberService.updateProfile(memberId, requestBody);
        return ResponseEntity.ok(BaseResponse.SUCCESS);
    }

    @Operation(summary = "로그아웃")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/logout")
    public ResponseEntity<BaseResponse> logout(@Auth Long memberId) {
        log.info("[logout] memberId = {}", memberId);
        BaseResponse response = memberService.logout(memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원 탈퇴")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/quit")
    public ResponseEntity<BaseResponse> quit(@Auth Long memberId) {
        log.info("[quit] memberId = {}", memberId);
//        BaseResponse response = memberService.quit(memberId);
        BaseResponse response = BaseResponse.SUCCESS;
        return ResponseEntity.ok(response);
    }
}
