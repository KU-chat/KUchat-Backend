package kuchat.server.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kuchat.server.domain.auth.argumentResolver.Auth;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.domain.Validator;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.DetailProfileResponse;
import kuchat.server.domain.member.dto.ProfileUpdateRequest;
import kuchat.server.domain.member.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static kuchat.server.common.response.BaseResponseStatus.NOT_FOUND_IMAGE;

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
    public ResponseEntity<BaseResponse> updateMyProfile(@Auth Member member,
                                                        @Validated @RequestBody ProfileUpdateRequest requestBody,
                                                        BindingResult bindingResult) {
        log.info("[updateMyProfile] 프로필 수정 요청 = {}", requestBody.toString());
        Validator.validateRequest(bindingResult);
        return profileService.updateProfile(member, requestBody);
    }

    @Operation(summary = "나의 프로필 사진 수정")
    @SecurityRequirement(name = "JWT")
    @PutMapping("/image")
    public ResponseEntity<BaseResponse> updateMyProfileImage(@Auth Member member,
                                                        @RequestParam MultipartFile multipartFile) {
        log.info("[updateMyProfileImage] 프로필 이미지 수정 요청 = {}", multipartFile.toString());
        return profileService.updateProfileImage(member, multipartFile);
    }

    @Operation(summary = "친구의 프로필 조회")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{memberId}")
    public ResponseEntity<BaseResponse> getFriendProfile(@Auth Member member,
                                                         @PathVariable("memberId") Long friendMemberId) {
        log.info("[getFriendProfile] 친구의 프로필 조회 요청 : memberId = {}, 친구의 memberId = {}",
                member.getId(), friendMemberId);
        return profileService.getFriendProfile(member.getId(), friendMemberId);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse> handleMissingImageException(MissingServletRequestParameterException e) {
        // 클라이언트에서 multipartFile 필드 이름을 잘못 지정했거나, 파일 자체를 전송하지 않은 경우
        log.error("[handleMissingImageException] 클라이언트 요청에서 multipartFile이 누락된 경우");
        HttpStatus httpStatus = NOT_FOUND_IMAGE.getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(new BaseResponse(NOT_FOUND_IMAGE));
    }

}
