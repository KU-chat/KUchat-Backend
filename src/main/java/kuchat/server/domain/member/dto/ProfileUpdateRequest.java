package kuchat.server.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter @ToString
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateRequest {
    private String name;
    private String plusId;
    private String department;
    private String firstLanguage;
    private String secondLanguage;
    private String profileImage;
    private String aboutMe;
}
