package kuchat.server.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.common.jwt.argumentResolver.Auth;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.Validator;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.DetailProfileResponse;
import kuchat.server.domain.member.dto.ProfileUpdateRequest;
import kuchat.server.domain.member.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Profile", description = "프로필")
@RequiredArgsConstructor
@RequestMapping("/profile")
@RestController
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "나의 프로필 조회")
    @SecurityRequirement(name = "JWT")
    @GetMapping
    public ResponseEntity<DetailProfileResponse> getMyProfile(@Auth Member member) {
        log.info("[getMyProfile] 나의 프로필 조회 요청 memberId = {}", member.getId());
        return profileService.getProfile(member);
    }

    @Operation(summary = "나의 프로필 수정")
    @SecurityRequirement(name = "JWT")
    @PutMapping
    public ResponseEntity<BaseResponseStatus> updateMyProfile(@Auth Member member,
                                                              @Validated @RequestBody ProfileUpdateRequest requestBody,
                                                              BindingResult bindingResult) {
        log.info("[updateMyProfile] 프로필 수정 요청 = {}", requestBody.toString());
        Validator.validateRequest(bindingResult);
        profileService.updateProfile(member.getId(), requestBody);
        return ResponseEntity.ok(BaseResponseStatus.SUCCESS);
    }
}
