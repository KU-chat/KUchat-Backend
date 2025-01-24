package kuchat.server.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kuchat.server.domain.enums.Gender;
import kuchat.server.domain.member.dto.ProfileUpdateRequest;
import kuchat.server.domain.member.dto.SignupRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;


@Getter
@Slf4j
@NoArgsConstructor
@Embeddable
public class Profile {
    @Column(name = "name")
    private String name;

    @Column(name = "department")
    private String department;

    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String hometown;

    private String profileImage;

    @Setter
    private String aboutMe;         // 한줄 자기소개

    public Profile(String profileImage) {
        this.profileImage = profileImage;
    }

    public void update(SignupRequest request) {
        this.hometown = request.getCountry();
        this.name = request.getName();
        this.department = request.getMajor();
        this.gender = Gender.of(request.getGender());
        this.birthday = request.getBirth();
    }

    public void update(ProfileUpdateRequest request, String defaultImage) {
        name = request.getName();
        department = request.getDepartment();
        aboutMe = request.getAboutMe();
        String profileImage = getProfileImage();
        if (profileImage == null || profileImage.isBlank()){
            this.profileImage = defaultImage;
        }
    }

    public int getAge() {
        LocalDate currentDate = LocalDate.now();                    // 현재 날짜
        Period age = Period.between(birthday, currentDate);        // 생일과 현재 날짜를 비교하여 나이를 계산
        return age.getYears();                                      // 현재 연도를 기준으로 나이를 반환
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return Objects.equals(getName(), profile.getName()) &&
                Objects.equals(getDepartment(), profile.getDepartment()) &&
                Objects.equals(getBirthday(), profile.getBirthday()) &&
                getGender() == profile.getGender() &&
                Objects.equals(getHometown(), profile.getHometown()) &&
                Objects.equals(getProfileImage(), profile.getProfileImage()) &&
                Objects.equals(getAboutMe(), profile.getAboutMe());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDepartment(), getBirthday(), getGender(), getHometown(), getProfileImage(), getAboutMe());
    }

    public void updateProfileImage(String newProfile) {
        this.profileImage = newProfile;
    }
}
