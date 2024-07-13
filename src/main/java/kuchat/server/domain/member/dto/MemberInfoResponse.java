package kuchat.server.domain.member.dto;

import kuchat.server.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoResponse {
    private Long id;
    private String name;
    private String profileImage;

    public MemberInfoResponse (Member member){
        this.id = member.getId();
        this.name = member.getName();
        this.profileImage = member.getProfileImage();
    }
}
