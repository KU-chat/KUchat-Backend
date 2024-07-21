package kuchat.server.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.common.argumentResolver.Auth;
import kuchat.server.domain.member.dto.ProfileResponse;
import kuchat.server.domain.member.dto.ProfileUpdateRequest;
import kuchat.server.domain.member.dto.SignupRequest;
import kuchat.server.domain.member.dto.SignupResponse;
import kuchat.server.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest signupRequest) {
        log.info("[signup] signupRequest = {}", signupRequest.toString());
        if (memberService.duplicateStudentId(signupRequest.getStudentId())) {
            System.out.println("[error] 이미 존재하는 학번입니다.");
        }
        SignupResponse response = memberService.signup(signupRequest);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/"));
//        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);     // 리다이렉트를 하려면 status를 SEE_OTHER(303)로 설정해야한다
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "나의 프로필 조회")
    @GetMapping("/my-profile")
    public ResponseEntity<ProfileResponse> getMyProfile(@Auth Long memberId) {
        log.info("[getMyProfile] 나의 프로필 조회 요청");
        log.info("[getMyProfile] memberId = {}", memberId);
        ProfileResponse response = memberService.getProfile(memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "나의 프로필 수정")
    @PatchMapping("/my-profile")
    public ResponseEntity<Void> updateMyProfile(@Auth Long memberId,
                                                @RequestBody ProfileUpdateRequest requestBody) {
        log.info("[getMyProfile] 나의 프로필 수정 요청");
        memberService.updateProfile(memberId, requestBody);
        return ResponseEntity.ok().build();
    }
}
