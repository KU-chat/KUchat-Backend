package kuchat.server.domain.member.dto;

import kuchat.server.domain.enums.Gender;
import kuchat.server.domain.member.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@ToString
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private String name;
    private String gender;
    private int age;
    private String department;
    private String hometown;
    private String profileImage;
    private String aboutMe;

    public ProfileResponse(Profile profile){
        this.name = profile.getName();
        this.gender = profile.getGender().getKorean();
        this.age = profile.getAge();
        this.department = profile.getDepartment();
        this.hometown = profile.getHometown();
        this.profileImage = profile.getProfileImage();
        this.aboutMe = profile.getAboutMe();
    }

}
