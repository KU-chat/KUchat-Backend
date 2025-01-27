package kuchat.server.domain.member.service;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.domain.S3Service;
import kuchat.server.domain.block.service.BlockService;
import kuchat.server.domain.friend.dto.FriendResponse;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.DetailProfileResponse;
import kuchat.server.domain.member.dto.ProfileImageUpdateResponse;
import kuchat.server.domain.member.dto.ProfileUpdateRequest;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static kuchat.server.common.response.BaseResponseStatus.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProfileService {
    private final MemberRepository memberRepository;
    private final BlockService blockService;
    private final S3Service s3Service;

    @Value("${default.profile.dirName}")
    private String profileImageDirName;

    public ResponseEntity<DetailProfileResponse> getProfile(Member member) {
        return ResponseEntity.ok(new DetailProfileResponse(member));
    }

    @Transactional
    public ResponseEntity<BaseResponse> updateProfile(Member member, ProfileUpdateRequest request) {
        validateAndUpdatePlusId(member.getId(), request.getPlusId());
        member.updateProfile(request);
        return ResponseEntity.ok(new BaseResponse(SUCCESS));
    }

    @Transactional
    public ResponseEntity<BaseResponse> updateProfileImage(Member member, MultipartFile multipartFile) {
        String oldProfile = member.getProfile().getProfileImage();
        log.info("[updateProfileImage] 기존 이미지 = {}", oldProfile);
        String newProfile = uploadAndUpdateImage(member, multipartFile);
        s3Service.deleteImage(oldProfile);
        return ResponseEntity.ok(new ProfileImageUpdateResponse(SUCCESS, newProfile));
    }

    private String uploadAndUpdateImage(Member member, MultipartFile multipartFile) {
        try {
            String newProfile = s3Service.uploadImage(multipartFile, profileImageDirName);
            log.info("[updateProfileImage] 새로운 이미지 = {}", newProfile);
            member.updateProfileImage(newProfile);
            return newProfile;
        } catch (IOException e) {
            throw new KuchatException(IMAGE_UPLOAD_FAIL);
        }
    }

    private void validateAndUpdatePlusId(Long memberId, String plusId) {
        Member member = getMember(memberId);
        memberRepository.findAllByPlusIdWithLock(plusId)
                .stream().findAny()
                .ifPresentOrElse(
                        duplicatedMember -> {
                            if (!duplicatedMember.equals(member)) {
                                throw new KuchatException(DUPLICATED_PLUSID);
                            }
                        },
                        () -> member.setPlusId(plusId)
                );
    }

    public ResponseEntity<BaseResponse> getFriendProfile(Long memberId, Long friendMemberId) {
        log.info("[getFriendProfile] id가 {} 인 친구의 프로필 조회", friendMemberId);
        Member friend = getMember(friendMemberId);
        if (blockService.isBlockOrBlocked(memberId, friendMemberId)) {
            throw new KuchatException(new BaseResponse(BLOCKED_MEMBER_PROFILE));
        }
        DetailProfileResponse friendProfileResponse = new DetailProfileResponse(friend);
        return ResponseEntity.ok(friendProfileResponse);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
    }
}
