package kuchat.server.domain.friend.dto;

import kuchat.server.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class ApplyResponse {
    private Long friendId;
    private String name;
    private String department;
    private String gender;
    private int age;
    private String aboutMe;
    private String hometown;
    private String profileImage;
    private LocalDateTime sentTime;

    public ApplyResponse(Member member, LocalDateTime sentTime) {
        friendId = member.getId();
        name = member.getName();
        department = member.getDepartment();
        gender = member.getGender().toString();
        age = member.getAge();
        aboutMe = member.getAboutMe();
        hometown = member.getHometown();
        profileImage = member.getProfileImage();
        this.sentTime = sentTime;
    }
}
