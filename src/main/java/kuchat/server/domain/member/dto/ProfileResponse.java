package kuchat.server.domain.member.dto;

import kuchat.server.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter @ToString
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private String name;
    private String gender;
    private int age;
    private String plusId;
    private String department;
    private String firstLanguage;
    private String secondLanguage;
    private String hometown;
    private String profileImage;
    private String aboutMe;

    public ProfileResponse(Member member) {
        name = member.getName();
        gender = member.getGender().toString();
        age = member.getAge();
        plusId = member.getPlusId();
        department = member.getDepartment();
        firstLanguage = member.getFirstLanguage().toString();
        secondLanguage = member.getSecondLanguage().toString();
        hometown = member.getHometown();
        profileImage = member.getProfileImage();
        aboutMe = member.getAboutMe();
    }
}
