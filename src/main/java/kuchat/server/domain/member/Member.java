package kuchat.server.domain.member;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import kuchat.server.domain.BaseTime;
import kuchat.server.domain.enums.Platform;
import kuchat.server.domain.enums.Role;
import kuchat.server.domain.enums.Status;
import kuchat.server.domain.member.dto.ProfileUpdateRequest;
import kuchat.server.domain.member.dto.SignupRequest;
import kuchat.server.domain.oauth.dto.GoogleInfoResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

@Getter
@Slf4j
@Entity
@Table(name = "member")
@NoArgsConstructor
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Email(message = "이메일 형식이 아닙니다.")
    @NotEmpty
    @Valid
    @Column(name = "email", nullable = false)
    private String email;

    @Size(min = 9, max = 9, message = "학번은 9자리 숫자 형태여야 합니다.")
    @Column(name = "student_id")
    private String studentId;

    @Setter
    @Column(name = "plus_id")
    private String plusId;

    @Embedded
    private Profile profile;

    @Embedded
    private Language language;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;

    @Column(nullable = false)
    private String providerId;       // 플랫폼에서 제공하는 id

    @Builder
    public Member(String email, Platform platform, String providerId, String profileImage) {
        this.email = email;
        this.platform = platform;
        this.providerId = providerId;
        this.profile = new Profile(profileImage);
        status = Status.PENDING;
        role = Role.GUEST;
    }

    public Member(GoogleInfoResponse infoResponse) {

    }

    public void updateInfo(SignupRequest request, String defaultImage) {
        this.language = new Language(request);
        profile.update(request, defaultImage);

        this.studentId = request.getStudentIdNumber();
        this.plusId = generatePlusId(10);
        this.status = Status.ACTIVE;
        this.role = Role.STUDENT;           // 추가정보 받은 후 처리
    }

    public String generatePlusId(int length) {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(CHARACTERS.charAt(secureRandom.nextInt(CHARACTERS.length())));
        }
        return stringBuilder.toString();
    }

    public void updateProfile(ProfileUpdateRequest request) {
        profile.update(request);
        language.update(request);
    }

    public String getName() {
        return profile.getName();
    }

    public void updateProfileImage(String newProfile) {
        profile.updateProfileImage(newProfile);
    }
}
