package kuchat.server.domain.relation.block.dto;

import kuchat.server.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BlockMemberResponse {
    private Long friendId;
    private String name;
    private String profileImage;

//    private String department;
//    private String gender;
//    private int age;
//    private String aboutMe;
//    private String hometown;

    public BlockMemberResponse(Member member) {
        friendId = member.getId();
        name = member.getName();
        profileImage = member.getProfileImage();
    }
}
